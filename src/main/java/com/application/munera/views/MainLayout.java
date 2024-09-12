package com.application.munera.views;

import com.application.munera.services.CSVService;
import com.application.munera.services.ExpenseService;
import com.application.munera.views.categories.CategoriesView;
import com.application.munera.views.dashboard.DashboardView;
import com.application.munera.views.events.EventsView;
import com.application.munera.views.expenses.ExpensesView;
import com.application.munera.views.people.PeopleView;
import com.application.munera.views.settings.SettingsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private final transient AuthenticationContext authContext;
    private final CSVService csvService;
    private final ExpenseService expenseService;

    public MainLayout(AuthenticationContext authContext, CSVService csvService, ExpenseService expenseService) {
        this.authContext = authContext;
        this.csvService = csvService;
        this.expenseService = expenseService;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // Retrieve the authenticated user's name
        String username = authContext.getPrincipalName().orElse("Guest");

        // Create the user icon
        final var userIcon = LineAwesomeIcon.USER.create();
        userIcon.addClassNames(LumoUtility.FontSize.LARGE); // Make the icon size larger

        // Create a Span to display "Hi, username"
        Span greeting = new Span("Hi, " + username);
        greeting.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD); // Make text larger and bold

        // Combine the user icon and greeting in a horizontal layout
        HorizontalLayout userInfoLayout = new HorizontalLayout(userIcon, greeting);
        userInfoLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center vertically

        // Create the logout button
        Button logout = new Button("Logout", click -> this.authContext.logout());
        logout.getStyle().set("padding", "10px"); // add padding to the logout button

        // Create the Export to CSV button
        Button exportToCSVButton = new Button("Export to CSV");
        exportToCSVButton.addClickListener(event -> {
            // Call a method in ExpensesView to handle CSV creation
            StreamResource resource = this.csvService.createCSVResource(this.expenseService.findAll());
            Anchor downloadLink = new Anchor(resource, "Download CSV");
            downloadLink.getElement().setAttribute("download", true);
            addToNavbar(downloadLink); // Add the download link to the navbar
        });

        // Create the header layout and add all elements
        HorizontalLayout header = new HorizontalLayout(exportToCSVButton, userInfoLayout, logout);
        header.setWidthFull(); // Make the header take the full width
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.END); // Align items to the right
        header.getStyle().set("padding", "0 10px"); // Add padding around the header

        addToNavbar(true, toggle, viewTitle);
        addToNavbar(header);
    }

    private void addDrawerContent() {
        Span appName = new Span("Munera");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Expenses", ExpensesView.class, LineAwesomeIcon.MONEY_BILL_SOLID.create()));
        nav.addItem(new SideNavItem("Categories", CategoriesView.class, LineAwesomeIcon.FOLDER.create()));
        nav.addItem(new SideNavItem("People", PeopleView.class, LineAwesomeIcon.USER.create()));
        nav.addItem(new SideNavItem("Events", EventsView.class, LineAwesomeIcon.BANDCAMP.create()));
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.CHART_LINE_SOLID.create()));
        nav.addItem(new SideNavItem("Settings", SettingsView.class, LineAwesomeIcon.COG_SOLID.create()));

        return nav;
    }

    private Footer createFooter() {
        return new Footer();
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}