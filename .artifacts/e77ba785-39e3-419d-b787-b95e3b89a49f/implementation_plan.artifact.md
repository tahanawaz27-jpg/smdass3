# Implementation Plan - TaskFlow Assignment 4

Refactor the TaskFlow Android app from Assignment 3 to replace all built-in `ListView`s with custom `RecyclerView`s, add section headers with grouping, swipe gestures (swipe-to-complete, swipe-to-delete with confirmation), and long-press drag-to-reorder for tasks, as well as swipe-to-delete with name confirmation for file attachments.

## User Review Required

> [!IMPORTANT]
> - **RecyclerView Migrations:** All three screens (`HomeActivity`, `TasksFragment`, `FilesFragment`) will transition from `ListView` + `BaseAdapter` to `RecyclerView` + modern adapters (`ListAdapter` with `DiffUtil` for projects, grouped multi-view adapters for tasks, and custom adapters for files).
> - **Task Gestures:** Tasks support swipe-left (complete), swipe-right (delete with AlertDialog confirmation), and long-press drag-to-reorder **strictly within the same status group** (Overdue, In progress, To do, Done). Headers are non-swiped and non-draggable.
> - **File Gestures:** Attachment rows support swipe-right to delete with a secure name confirmation dialog (user must type the exact attachment display name).

## Proposed Changes

### Build Configuration
#### [MODIFY] [build.gradle](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/build.gradle)
- Add `androidx.recyclerview:recyclerview` dependency.

### Home Screen (`HomeActivity`)
#### [MODIFY] [activity_home.xml](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/res/layout/activity_home.xml)
- Replace `ListView` (`lv_projects`) with `androidx.recyclerview.widget.RecyclerView`.
#### [MODIFY] [ProjectAdapter.java](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/java/com/example/smd_assignment03/ProjectAdapter.java)
- Migrate from `BaseAdapter` to `ListAdapter<Project, ProjectAdapter.ProjectViewHolder>` utilizing `DiffUtil.ItemCallback<Project>`.
#### [MODIFY] [HomeActivity.java](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/java/com/example/smd_assignment03/HomeActivity.java)
- Setup RecyclerView with `LinearLayoutManager` and `ProjectAdapter`.
- Use `adapter.submitList()` instead of `notifyDataSetChanged()`.
- Update project by ID when returning from `ProjectActivity`.

### Tasks Screen (`TasksFragment`)
#### [NEW] [item_task_header.xml](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/res/layout/item_task_header.xml)
- XML layout for section headers showing status name and task count.
#### [MODIFY] [fragment_tasks.xml](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/res/layout/fragment_tasks.xml)
- Replace `ListView` (`lv_tasks`) with `androidx.recyclerview.widget.RecyclerView`.
#### [MODIFY] [TaskAdapter.java](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/java/com/example/smd_assignment03/TaskAdapter.java)
- Migrate from `BaseAdapter` to `RecyclerView.Adapter` handling two view types (`TYPE_HEADER = 0`, `TYPE_TASK = 1`).
- Support section headers and task rows.
#### [MODIFY] [TasksFragment.java](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/java/com/example/smd_assignment03/TasksFragment.java)
- Group tasks using `TaskGroupingHelper` (fixed order: Overdue, In progress, To do, Done) into `List<TaskListItem>` and submit to `TaskAdapter`.
- Implement `ItemTouchHelper` callback for:
  - **Swipe Left:** Mark task as Done (move to Done group, update counts. If already Done, show Toast "Task already completed").
  - **Swipe Right:** Delete task after confirmation dialog. Restore row if cancelled.
  - **Long-press Drag-and-Drop:** Reorder tasks **only within the same status group**. Prevent cross-group dragging or dragging headers.
  - Ensure headers cannot be swiped or dragged.

### Files Screen (`FilesFragment`)
#### [MODIFY] [fragment_files.xml](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/res/layout/fragment_files.xml)
- Replace `ListView` (`lv_files`) with `androidx.recyclerview.widget.RecyclerView`.
#### [MODIFY] [AttachmentAdapter.java](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/java/com/example/smd_assignment03/AttachmentAdapter.java)
- Migrate from `BaseAdapter` to `RecyclerView.Adapter<AttachmentAdapter.ViewHolder>`.
#### [MODIFY] [FilesFragment.java](file:///C:/Users/Hp/OneDrive/Desktop/smdass3-main/app/src/main/java/com/example/smd_assignment03/FilesFragment.java)
- Implement `ItemTouchHelper` callback for swipe-right to delete attachments with name confirmation dialog. Restore row if cancelled or name incorrect.

## Verification Plan

### Automated Tests
- Run Gradle build (`gradle_build("app:assembleDebug")`) to ensure compilation success.

### Manual Verification
- Deploy app to emulator/device.
- Verify Home screen RecyclerView with project list and smooth updates.
- Verify Tasks screen grouped RecyclerView with section headers, swipe-to-complete, swipe-to-delete with confirmation, and intra-group drag-to-reorder.
- Verify Files screen RecyclerView with swipe-to-delete and name confirmation.
