# TaskFlow: Project Management App (SMD Assignment 3)

TaskFlow is a feature-rich Project Management Android application built with **Java** and **XML** for the Software for Mobile Devices (SMD) course.

## Features & Implementation Highlights

1. **Welcome & Home Screen (`SplashActivity`, `HomeActivity`)**:
   - Clean onboarding splash screen leading to the Home workspace.
   - Workspace summary ("Sports App") and project ListView.
   - Case-insensitive search bar filtering projects by name or tag.
   - **+ New Project Dialog**: Create new projects with validation (Name 1–60 chars, Tag 1–20 chars, Description 1–500 chars, whitespace trimming) and optional initial attachment selection from the catalog.

2. **Project Workspace (`ProjectActivity`, `TabLayout`)**:
   - Header with project title, tag, collaborator avatars (`JD`, `AS`, `MK`), and **Invite** action UI.
   - Horizontal scrollable `TabLayout` switching between **Overview**, **Tasks**, **Files**, and **Discussion**.

3. **Project Overview (`OverviewFragment`)**:
   - Brief & scope section with **Edit Project** dialog and validation.
   - Project progress summary and dynamic completion percentage.
   - **Task Breakdown** cards (To Do, In Progress, Done, Overdue) that directly filter the Tasks tab upon clicking.
   - Quick links to Files and Discussion.
   - **+ Add Task** shortcut.

4. **Task Management (`TasksFragment`, `TaskAdapter`)**:
   - Comprehensive task search over descriptions.
   - Multi-option filters: Status (`All statuses`, `To do`, `In progress`, `Done`), Priority (`High`, `Normal`, `Low`), Date (`All dates`, `Today`, `Overdue`, `No due date`).
   - Sorting options and **Reset filters**.
   - Completion checkboxes that instantly update project progress.
   - **Add & Edit/Delete Task Dialogs**: Character counter (0/500), priority/status selectors, and `DatePickerDialog` integration for optional due dates.

5. **Attachment Catalog (`FilesFragment`)**:
   - Searchable attachment links with type filters (`All`, `Images`, `Text`).
   - **Add Attachment**: Catalog picker linking actual files.
   - **Preview Dialog**: Modal image/text preview of actual mock content.
   - **Secure Removal**: Confirmation dialog requiring exact file name matching to delete attachments.

6. **Discussion (`DiscussionFragment`)**:
   - Clean discussion view showing "Coming Soon" as per assignment specifications.

---

## Commit History (Incremental Development)
- **Commit 1**: Splash screen, data models (`Project`, `Task`, `Attachment`), and Home screen project list.
- **Commit 2**: New project creation dialog, `ProjectActivity`, `TabLayout`, and `OverviewFragment`.
- **Commit 3**: Full `TasksFragment` (search, filters, sorting, checkboxes) and `FilesFragment` (catalog attachments, preview, removal confirmation).
- **Commit 4**: Refined FilesFragment implementation and layout stability.
- **Commit 5**: Added project toolbar collaborator avatars and Invite control UI.
- **Commit 6**: Implemented optional attachment selector in New Project creation dialog.
- **Commit 7**: Fixed ListView item click focus issue on project cards by blocking descendant focusability.
- **Commit 8**: Implemented Overview task breakdown card filter delegation to TasksFragment.
- **Commit 9**: Implemented task edit and deletion dialog when clicking task rows in TasksFragment.
- **Commit 10**: Added comprehensive project `README.md` documentation and final polish.
