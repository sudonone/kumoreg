package org.kumoricon.site.utility.testbadge;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeFactory;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerService;
import org.kumoricon.service.print.BadgePrintService;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.PrintBadgeHandler;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.print.PrintException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Controller
@Scope("request")
public class TestBadgePresenter implements PrintBadgeHandler {
    private static final Logger log = LoggerFactory.getLogger(BadgePrintService.class);
    private final AttendeeFactory attendeeFactory;
    private final BadgePrintService badgePrintService;
    private final ComputerService computerService;
    private final BadgeRepository badgeRepository;

    @Autowired
    public TestBadgePresenter(BadgePrintService badgePrintService, AttendeeFactory attendeeFactory, ComputerService computerService, BadgeRepository badgeRepository) {
        this.badgePrintService = badgePrintService;
        this.attendeeFactory = attendeeFactory;
        this.computerService = computerService;
        this.badgeRepository = badgeRepository;
    }

    /**
     * Show the attendee badge window with automatically generated badges for all badge types.
     * Generates badges Adult - Child - Youth
     * @param view View
     * @param xOffset Horizontal offset in points
     * @param yOffset Vertical offset in points
     */
    public void showAttendeeBadgeWindow(AttendeePrintView view, Integer xOffset, Integer yOffset) {
        if (xOffset == null) { xOffset = 0; }
        if (yOffset == null) { yOffset = 0; }

        log.info("{} generating test badges for all badge types with horizontal offset {} vertical offset {}",
                view.getCurrentUsername(), xOffset, yOffset);
        List<Attendee> attendees = new ArrayList<>();

        for (Badge badge : badgeRepository.findByVisibleTrue()) {
            attendees.add(attendeeFactory.generateDemoAttendee(badge));
            attendees.add(attendeeFactory.generateYouthAttendee(badge));
            attendees.add(attendeeFactory.generateChildAttendee(badge));
        }

        log.info("{} printing test badges for {}", view.getCurrentUsername(), attendees);
        showAttendeeBadgeWindow(view, attendees, xOffset, yOffset);
    }



    /**
     * Show the attendee badge window with the given number of automatically generated badges.
     * Generates badges Adult - Child - Youth
     * @param view View
     * @param numberOfBadges Number of badges to generate, minimum: 1, maximum: 3)
     * @param xOffset Horizontal offset in points
     * @param yOffset Vertical offset in points
     */
    public void showAttendeeBadgeWindow(AttendeePrintView view, Integer numberOfBadges, Badge badge, Integer xOffset, Integer yOffset) {
        if (numberOfBadges == null) { numberOfBadges = 1; }
        if (xOffset == null) { xOffset = 0; }
        if (yOffset == null) { yOffset = 0; }

        log.info("{} generating {} test badge(s) with horizontal offset {} vertical offset {}",
                view.getCurrentUsername(), numberOfBadges, xOffset, yOffset);
        List<Attendee> attendees = new ArrayList<>();

        if (numberOfBadges >= 1) { attendees.add(attendeeFactory.generateDemoAttendee(badge)); }
        if (numberOfBadges >= 2) { attendees.add(attendeeFactory.generateYouthAttendee(badge)); }
        if (numberOfBadges >= 3) { attendees.add(attendeeFactory.generateChildAttendee(badge)); }

        log.info("{} printing test badges for {}", view.getCurrentUsername(), attendees);
        showAttendeeBadgeWindow(view, attendees, xOffset, yOffset);
    }

    public void showCurrentOffsets(TestBadgeView view, String ipAddress) {
        Computer currentClient = computerService.findComputerByIP(ipAddress);
        if (currentClient != null) {
            view.setXOffset(currentClient.getxOffset());
            view.setYOffset(currentClient.getyOffset());
        }
    }

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
        if (attendeeList == null) { return; }
        printBadges((BaseView) view, attendeeList, null, null);
        view.showPrintBadgeWindow(attendeeList);
    }

    private void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList, Integer xOffset, Integer yOffset) {
        if (attendeeList == null) { return; }
        printBadges((BaseView) view, attendeeList, xOffset, yOffset);
        view.showPrintBadgeWindow(attendeeList);
    }



    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        if (printBadgeWindow != null) {
            log.info("{} reports test badges printed successfully for {}",
                    printBadgeWindow.getParentView().getCurrentUser(), attendees);
            printBadgeWindow.close();
        }
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        if (printBadgeWindow == null) {
            return;
        }
        TestBadgeView view = (TestBadgeView) printBadgeWindow.getParentView();
        if (attendeeList.size() > 0) {
            log.info("{} reprinting test badges for {}",
                    view.getCurrentUsername(), attendeeList, view.getXOffset(), view.getYOffset());
            view.notify("Reprinting badges");
            printBadges(view, attendeeList, view.getXOffset(), view.getYOffset());
        } else {
            view.notify("No attendees selected");
        }
    }

    @Override
    public BadgePrintFormatter getBadgeFormatter(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        TestBadgeView view = (TestBadgeView) printBadgeWindow.getParentView();
        return badgePrintService.getCurrentBadgeFormatter(attendees, view.getXOffset(), view.getYOffset(), LocalDate.now());
    }

    /**
     * Print badges for the given attendees and display any error or result messages
     * @param view Current view
     * @param attendeeList Attendees to print badges for
     * @param xOffset Printing X offset in points (1/72 inch)
     * @param yOffset printing Y offset in points (1/72 inch)
     */
    private void printBadges(BaseView view, List<Attendee> attendeeList, Integer xOffset, Integer yOffset) {
        try {
            String result = badgePrintService.printBadgesForAttendees(
                    attendeeList, view.getCurrentClientIPAddress(), xOffset, yOffset, LocalDate.now());
            view.notify(result);
        } catch (PrintException e) {
            log.error("Error printing badges for {}", view.getCurrentUsername(), e);
            view.notifyError(e.getMessage());
        }
    }

    public List<Badge> getBadges() {
        return badgeRepository.findAll();
    }
}
