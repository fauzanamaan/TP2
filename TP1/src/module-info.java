module FoundationsF25 {
    requires javafx.controls;
    requires java.sql;

    // Main app
    opens applicationMain to javafx.graphics, javafx.fxml;
    exports applicationMain;

    // Admin + user flows
    opens guiAdminHome to javafx.base;
    opens guiUserLogin to javafx.base;
    opens guiUserUpdate to javafx.base;
    opens guiDeleteUser to javafx.base;
    opens guiAddRemoveRoles to javafx.base;

    // Existing working pages
    opens guiListUsers to javafx.base;
    opens guiManageInvitations to javafx.base;

    // Role pages
    opens guiRole1 to javafx.base;
    opens guiRole2 to javafx.base;

    // Post system
    opens guiViewPost to javafx.base;

    // (Optional but safe)
    opens entityClasses to javafx.base;
}
