package org.kumoricon.site;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ServiceException;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.attendee.reg.OrderView;
import org.kumoricon.site.attendee.search.byname.SearchByNameView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView(name = HomeView.VIEW_NAME)
public class HomeView extends BaseGridView implements View {
    public static final String VIEW_NAME = "";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private HomePresenter handler;

    private Label priceList = new Label("");

    private ButtonField txtSearch = new ButtonField();
    private Button btnNewReg = new Button("New Registration");

    @PostConstruct
    void init() {
        setColumns(5);
        setRows(3);

        addComponent(txtSearch, 2, 0);
        txtSearch.setButtonCaption("Search");

        addComponent(btnNewReg, 3, 1);
        addComponent(priceList, 2, 1, 2, 2);


        txtSearch.setPlaceholder("Search Attendees by Name...");
        txtSearch.focus();

        txtSearch.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        txtSearch.addClickListener(e -> {
            String searchString = txtSearch.getValue().trim();
           if (!searchString.isEmpty()) {
               navigateTo(SearchByNameView.VIEW_NAME + "/" + searchString);
           }
        });

        btnNewReg.addClickListener(e -> navigateTo(OrderView.VIEW_NAME));

        priceList.setContentMode(ContentMode.HTML);
        setColumnExpandRatio(0, 10);
        setColumnExpandRatio(1, 1);
        setColumnExpandRatio(2, 5);
        setColumnExpandRatio(3, 1);
        setColumnExpandRatio(4, 10);
        handler.showBadges(this);
    }



    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void showBadges(List<Badge> badges) {
        /* TODO: Make this look better. Border on the table, bold header row, right-align numbers, etc
          Add any CSS classes to styles.scss. */
        StringBuilder output = new StringBuilder();
        output.append("<table class=\"kumoTable\">");
        output.append("<tr>");
        output.append("<th>Badge Type</td>");
        output.append("<th>Adult (18+)</td>");
        output.append("<th>Youth (13 - 17)</td>");
        output.append("<th>Child (6 - 12)</td>");
        output.append("<th>5 and Under</td>");
        output.append("</tr>");
        for (Badge badge : badges) {
            try {
                output.append("<tr>");
                output.append("<td>" + badge.getName() + "</td>");
                output.append("<td>$" + badge.getCostForAge(35L) + "</td>");
                output.append("<td>$" + badge.getCostForAge(17L) + "</td>");
                output.append("<td>$" + badge.getCostForAge(11L) + "</td>");
                output.append("<td>$" + badge.getCostForAge(4L) + "</td>");
                output.append("</tr>");
            } catch (ServiceException e) {
                notifyError("Error getting age ranges for badge " + badge.getName());
            }
        }
        output.append("</table>");
        priceList.setValue(output.toString());
    }
}