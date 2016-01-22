package org.kumoricon.presenter.order;

import com.vaadin.ui.Notification;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.view.attendee.AttendeeDetailForm;
import org.kumoricon.view.order.AttendeeWindow;
import org.kumoricon.view.order.OrderView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
@Scope("request")
public class OrderPresenter {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;

    private OrderView view;

    public OrderPresenter() {
    }

    public void createNewOrder() {
        Order order = new Order();
        order.setOrderId(order.generateOrderId());
        orderRepository.save(order);
        KumoRegUI.getCurrent().getNavigator().navigateTo(view.VIEW_NAME + "/" + order.getId());
    }

    public void showOrder(int id) {
        Order order = orderRepository.findOne(id);
        if (order != null) {
            view.afterSuccessfulFetch(order);
        } else {
            Notification.show("Error: order " + id + " not found.");
        }
    }

    public void cancelOrder() {
        // Todo: Remove from database if order hasn't been saved yet? Make sure to not
        // delete orders that are already paid for. Not sure if this is a good feature
        // or not
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    public OrderView getView() { return view; }
    public void setView(OrderView view) { this.view = view; }

    public void addNewAttendee() {
        Attendee newAttendee = new Attendee();
        newAttendee.setOrder(view.getOrder());
        AttendeeWindow attendeeWindow = new AttendeeWindow(this);
        KumoRegUI.getCurrent().addWindow(attendeeWindow);


        AttendeeDetailForm form = attendeeWindow.getDetailForm();
        form.setAvailableBadges(badgeRepository.findAll());
        form.show(newAttendee);
    }

    public void addAttendeeToOrder(Attendee attendee) {
        Order order = view.getOrder();
        attendee.setOrder(order);
        order.addAttendee(attendee);
        order.setTotalAmount(getOrderTotal(order));
        orderRepository.save(order);
        view.afterSuccessfulFetch(order);
    }

    private static BigDecimal getOrderTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (Attendee a : order.getAttendeeList()) {
            total = total.add(a.getPaidAmount());
        }
        return total;
    }

    public void removeAttendeeFromOrder(Attendee attendee) {
        if (attendee != null && !attendee.isCheckedIn()) {
            String name = attendee.getName();
            Order order = view.getOrder();
            order.removeAttendee(attendee);
            attendee.setOrder(null);

            order.setTotalAmount(getOrderTotal(order));
            orderRepository.save(order);
            attendeeRepository.delete(attendee);
            Notification.show(name + " deleted");
            view.afterSuccessfulFetch(order);
        }
    }

    public void takeMoney() {
        Order currentOrder = view.getOrder();
        if (currentOrder.getAttendeeList().size() == 0) {
            Notification.show("Error: No attendees in order");
            return;
        }
        if (currentOrder.getPaymentType() == null) {
            Notification.show("Error: Payment type not selected");
            return;
        }

        // Print badges here
        currentOrder.paymentComplete();
        orderRepository.save(currentOrder);
        KumoRegUI.getCurrent().getNavigator().navigateTo("/");
        Notification.show("Order complete");
    }

    public void selectAttendee(Attendee attendee) {
        AttendeeWindow attendeeWindow = new AttendeeWindow(this);
        KumoRegUI.getCurrent().addWindow(attendeeWindow);
        AttendeeDetailForm form = attendeeWindow.getDetailForm();
        form.setAvailableBadges(badgeRepository.findAll());
        form.show(attendee);
    }
}