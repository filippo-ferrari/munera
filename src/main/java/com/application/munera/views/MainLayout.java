package com.application.munera.views;

import com.application.munera.views.categories.CategoriesView;
import com.application.munera.views.dashboard.DashboardView;
import com.application.munera.views.events.EventsView;
import com.application.munera.views.expenses.*;
import com.application.munera.views.people.PeopleView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private final transient AuthenticationContext authContext;

    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // Creating the logout button
        Button logout = new Button("Logout", click -> this.authContext.logout());

        // Adding some padding to the logout button
        logout.getStyle().set("padding", "10px");

        // Creating the header and adding the logout button to the far left
        HorizontalLayout header = new HorizontalLayout(logout);
        header.setWidthFull(); // Make the header take the full width
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.END); // Align items to the start (left)
        header.getStyle().set("padding", "0 10px"); // Add padding around the header if needed


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
