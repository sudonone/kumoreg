package org.kumoricon.site.attendee.search.bybadge;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.site.attendee.PrintBadgeView;
import org.kumoricon.site.attendee.search.PrintBadgePresenter;
import org.kumoricon.site.attendee.search.byname.SearchByNameView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchByBadgeRePrintBadgeView.TEMPLATE)
public class AttendeeSearchByBadgeRePrintBadgeView extends PrintBadgeView implements View {
    public static final String VIEW_NAME = "attendeeSearch";
    public static final String REQUIRED_RIGHT = "reprint_badge";

    public static final String TEMPLATE = "attendeeSearchByBadge/{badgeType}/{attendeeId}/reprint";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected Integer attendeeId;
    protected String searchString;

    @Autowired
    public AttendeeSearchByBadgeRePrintBadgeView(PrintBadgePresenter handler) {
        super(handler);
    }

    @Override
    protected void printedSuccessfullyClicked() {
        if (attendee.getOrder() != null) {
            navigateTo(SearchByNameView.VIEW_NAME + "/" + attendee.getOrder().getOrderId());
        } else {
            navigateTo(SearchByNameView.VIEW_NAME);
        }
    }

    @Override
    protected void reprintClicked() {
        handler.reprintBadge(this, attendeeId);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        this.searchString = map.get("badgeType");
        try {
            attendeeId = Integer.parseInt(map.get("attendeeId"));
        } catch (NumberFormatException ex) {
            notifyError("Bad attendee id: must be an integer");
            close();
        }
        handler.showAttendee(this, attendeeId);
    }

    @Override
    public void close() {
        if (attendeeId != null) {
            navigateTo(SearchByBadgeView.VIEW_NAME + "/" + searchString + "/" + attendeeId);
        } else {
            navigateTo(SearchByBadgeView.VIEW_NAME + "/" + searchString);
        }
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
