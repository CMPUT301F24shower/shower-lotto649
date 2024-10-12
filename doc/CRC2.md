### **Models CRC Cards**

| **Model** | **Responsibilities** | **Collaborators** |
|-|-|-|
| **Entrant** | - Join/leave waiting lists for events. <br> - Manage personal info (name, email, phone, profile picture). <br> - Upload, remove, use deterministic profile picture. <br> - Receive/manage notifications. <br> - Accept/decline invitations. <br> - View event details via QR codes. <br> - Device-based identification. | - Event <br> - NotificationSys <br> - QRCode <br> - Security |
| **Organizer** | - Create/manage events. <br> - Upload/update event posters. <br> - Generate/store QR codes. <br> - View/manage entrants on waiting lists. <br> - Set limits for entrants. <br> - Send notifications to entrants. <br> - Select/replace entrants for events. <br> - View selected, cancelled, and final attendees. | - Event <br> - NotificationSys <br> - QRCode <br> - Entrant <br> - Facility |
| **Admin** | - Remove inappropriate events, profiles, images, facilities, hashed QR codes. <br> - Browse events, profiles, images. | - Event <br> - Profile <br> - Image <br> - QRCode <br> - Facility |
| **Event** | - Store title and location of the event. <br> - Store cost, description, number of spots, and event type. <br> - Store poster image and QR code. <br> - Manage waiting list entrants. <br> - Generate a QR code for the event. | - Facility <br> - Organizer <br> - Entrant <br> - NotificationSys <br> - QRCode |
| **Facility** | - Store name, address, and description. <br> - Store organizer info. <br> - Manage events taking place at this facility. <br> - Scan QR codes for events at the facility. | - Event <br> - Organizer <br> - QRCode |
| **NotificationSys** | - Send notifications for event outcomes (selected, declined, replaced). <br> - Manage opt-in/opt-out preferences for notifications. | - Entrant <br> - Organizer <br> - Event |
| **QRCode** | - Generate unique QR codes for events. <br> - Scan QR Code to bring you to the event <br> - Store/remove QR code data in database. | - Event <br> - Organizer <br> - Entrant <br> - Admin |
| **Security Manager**| - Handle device-based identification for entrants. <br> - Ensure secure access without usernames/passwords. | - Entrant <br> - Event |

---

### **Controllers CRC Cards**

| **Controller** | **Responsibilities** | **Collaborators** |
|-|-|-|
| **EntrantController** | - Handle entrant registration and waiting list management. <br> - Accept/decline event invitations. <br> - Update entrant profiles. <br> - Handle QR code scanning for event details. | - Entrant <br> - Event <br> - NotificationSys <br> - QRCode <br> - ProfileView <br> - AddEditProfile <br> - FindEventView <br> - EventView |
| **OrganizerController** | - Manage event creation, updates, and deletion.<br> - Generate and store event QR codes.<br> - View/manage waiting list and participants.<br> - Send notifications. <br> - Manage organizer profile. | - Event <br> - QRCode <br> - NotificationSys <br> - Organizer <br> - FindEventView <br> - EventView <br> - AddEditEvent <br> - ProfileView <br> - AddEditProfile <br> - WaitingListView|
| **AdminController** | - Handle administrative tasks like removing events, profiles, and images.<br> - Browse the event and profile lists.<br> - Remove policy-violating content (QR codes, facilities). | - Event <br> - QRCode <br> - Facility <br> - Admin <br> - AdminDashboardView <br> - ProfilesView <br> - ProfileView <br> - AddEditProfileView <br> - FindEventView <br> - AddEditEventView <br> - FacilitiesView <br> - FacilityView <br> - AddEditFacilityView <br> - QRCodeScanFragment |
| **EventController** | - Manage event life cycle (create, update, delete).<br> - Handle event posters and waiting lists.<br> - Replace participants if they decline invitations. | - Event <br> - NotificationSys <br> - Entrant <br> - Organizer <br> - FindEventView <br> - EventView <br> - AddEditEventView <br> - FacilityView |
| **FacilityController** | - Manage facility life (create, update, delete). | - Facility <br> - Organizer <br> - FacilitiesView <br> - FacilityView <br> - AddEditFacilityView|
| **NotificationController**| - Send notifications for event status (selected, declined, replaced).<br> - Manage notification preferences. <br> - Allow users to opt in/out of receiving notifications. | - NotificationSys <br> - Entrant <br> - Organizer <br> - InvitationResponseFragment |
| **QRCodeController** | - Generate and manage QR codes for events. <br> - Store and delete hashed QR codes.<br> - Handle QR code scanning for event sign-up. | - Event <br> - Entrant <br> - QRCode <br> - QRCodeScanFragment|
| **SecurityController** | - Handle device-based identification and authentication. <br> - Ensure secure access for entrants without needing passwords. | - Entrant <br> - Security <br> - ProfileView <br> - AddEditProfileView|

---

### **Views CRC Cards**

| **View** | **Responsibilities** | **Collaborators** |
|-|-|-|
| **HomeView**|-|-|
| **ProfilesView** | - Display profiles. <br> - Allow updating profile details. <br> - Delete profiles. | - Admin <br> - Notification |
| **ProfileView** | - Display profile information (name, email, phone, profile picture).<br> - Allow updating profile details. <br> - Opt in/out of receiving notifications from organizers.| - Entrant <br> - Notification |
| **AddEditProfileView** | - Display profile details. <br> - Add/Edit profile. <br> - Delete profile. | - Entrant <br> - Organizer |
| **WaitingListView** | - Display waiting list for events. | - Event <br> - Organizer |
| **InvitationResponseFragment** | - Display event invitation status.<br> - Allow entrants to accept or decline invitations. | - Event <br> - Entrant |
| **FindEventView** | - Show a list of users events. <br> - Display event statistics, waiting lists. | - Event <br> - Entrant <br> - Organizer |
| **EventView** | - Display event details. <br> - Show event poster and description. <br> - Maps of entrant sign-ups if organizer for event | - Event <br> - Organizer <br> - QRCode |
| **AddEditEventView** | - Display event details. <br> - Add/Edit events. <br> - Delete Events. | - Event |
| **AdminDashboardView** | - Display list of all events, profiles, and images. <br> - Provide interface for removing inappropriate content (events, profiles). | - Event |
| **FacilitiesView** | - Display facility loction and number of active events. | - Facility |
| **FacilityView** | - Display facility details and events. | - Facility <br> - Event |
| **AddEditFacilityView** | - Display facility details. <br> - Add/Edit facility. <br> - Delete facility. | - Facility |
| **QRCodeScanFragment** | - Interface for scanning QR codes to view event details or sign up. | - QRCode <br> - Event |

---