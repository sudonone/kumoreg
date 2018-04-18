package org.kumoricon.site.attendee.reg;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.Payment;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.attendee.PaymentHandler;
import org.kumoricon.site.attendee.PrintBadgeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ViewScope
@SpringView(name = OrderPrintView.TEMPLATE)
public class OrderPrintView extends BaseGridView implements View {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    public static final String TEMPLATE = "order/{orderId}/print";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    private Button btnPrintedSuccessfully = new Button("Printed Successfully");
    private Button btnReprint = new Button("Reprint Selected");
    private Button btnClose = new Button("Close");
    private Grid<Attendee> attendeeGrid = new Grid<>();

    protected Integer orderId;
    protected Order order;
    private OrderPresenter orderPresenter;

    @Autowired
    public OrderPrintView(OrderPresenter orderPresenter) {
        this.orderPresenter = orderPresenter;
    }

    @PostConstruct
    public void init() {
        setColumns(5);
        setRows(5);

        setColumnExpandRatio(0, 10);
        setColumnExpandRatio(1, 2);
        setColumnExpandRatio(2, 1);
        setColumnExpandRatio(3, 1);
        setColumnExpandRatio(4, 10);

        attendeeGrid.addColumn(Attendee::getFirstName).setCaption("First Name");
        attendeeGrid.addColumn(Attendee::getLastName).setCaption("Last Name");
        attendeeGrid.addColumn(Attendee::getCheckedIn).setCaption("Badge Printed");

        attendeeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        addComponent(attendeeGrid, 1, 0, 1, 3);

        btnPrintedSuccessfully.addClickListener(clickEvent -> printedSuccessfullyClicked());
        btnReprint.addClickListener(clickEvent -> reprintClicked());
        addComponent(btnPrintedSuccessfully, 3, 0);

        btnClose.addClickListener((Button.ClickListener) clickEvent -> close());
        addComponent(btnClose, 3, 3);
    }

    protected void printedSuccessfullyClicked() {
        orderPresenter.badgePrintSuccess(this, order.getAttendees());
    }

    protected void reprintClicked() {
        List<Attendee> selectedAttendees = new ArrayList<>(attendeeGrid.getSelectedItems());
        orderPresenter.reprintBadges(this, selectedAttendees);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        try {
            this.orderId = Integer.parseInt((map.get("orderId")));
        } catch (NumberFormatException ex) {
            notifyError("Bad order id: must be an integer");
            navigateTo("/");
        }

        orderPresenter.showBadges(this, orderId);
    }

    @Override
    public void close() {
        navigateTo(OrderView.VIEW_NAME + "/" + orderId);
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    public void showOrder(Order order) {
        this.order = order;
        attendeeGrid.setItems(order.getAttendees());
        for (Attendee attendee : order.getAttendees()) {
            attendeeGrid.select(attendee);
        }
    }

    Order getOrder() {
        return this.order;
    }

}
