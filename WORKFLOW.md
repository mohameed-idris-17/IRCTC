# IRCTC Train Ticket Booking - Complete Workflow

## 📋 Project Overview Flow

```
User Opens Application
         ↓
    Display Menu
         ↓
   User Selects Option (1-7)
         ↓
    Execute Functionality
         ↓
    Return to Menu (until Exit)
```

---

## 🔄 Detailed User Journey Workflows

### 1️⃣ Sign Up Flow

```
START: User selects option 1
    ↓
Enter name
    ↓
Enter password
    ↓
Validate input (not empty)
    ↓
    ├─ Valid ────────────────────┐
    │                            │
    ↓                            ↓
Hash password with BCrypt    Display error
    ↓                        "Cannot be empty"
Create User object               ↓
  - UUID generated            Return to menu
  - Empty ticket list
    ↓
Add to userList
    ↓
Save to users.json
    ↓
Display "Sign up successful"
    ↓
Prompt to login
    ↓
Return to menu
```

**Data Flow:**
```
Input: name, password
  ↓
Process: BCrypt.hashpw(password, BCrypt.gensalt())
  ↓
Create: User(name, password, hashedPassword, UUID, [])
  ↓
Store: users.json ← userList.add(user)
  ↓
Output: Success message
```

---

### 2️⃣ Login Flow

```
START: User selects option 2
    ↓
Enter name
    ↓
Enter password
    ↓
Validate input (not empty)
    ↓
    ├─ Valid ────────────────────┐
    │                            │
    ↓                            ↓
Load users.json              Display error
    ↓                            ↓
Search for user by name      Return to menu
    ↓
Found user?
    ├─ Yes ──────────────────────┐
    │                            │
    ↓                            ↓ No
Compare passwords            Display error
BCrypt.checkpw(input, hashed)  "Invalid credentials"
    ↓                            ↓
Match?                       Return to menu
    ├─ Yes ──────────────┐
    │                    │
    ↓                    ↓ No
Set user session      Display error
    ↓                    ↓
Display "Welcome!"    Return to menu
    ↓
Return to menu
```

**Authentication Process:**
```
users.json
    ↓
Load List<User>
    ↓
Stream filter by name
    ↓
Found? → BCrypt.checkpw(plaintext, stored_hash)
    ↓
    ├─ Match → Set this.user = foundUser
    │          ↓
    │       Session Active
    │
    ├─ No Match → Throw IOException
                   ↓
                Display error
```

---

### 3️⃣ Fetch Bookings Flow

```
START: User selects option 3
    ↓
Check if logged in
    ↓
    ├─ Yes ─────────────────┐
    │                       │
    ↓                       ↓ No
Get user.TicketBooking   Display error
    ↓                    "Please login first"
Empty?                       ↓
    ├─ Yes ─────────┐    Return to menu
    │               │
    ↓               ↓ No
Display          Display each ticket:
"No tickets"     - Ticket ID
    ↓            - User ID
Return to menu   - Source → Destination
                 - Travel date
                     ↓
                 Return to menu
```

**Data Access:**
```
user (session object)
    ↓
user.getTicketBooking() → List<Ticket>
    ↓
For each ticket:
    ticket.getTicketInfo()
        ↓
    Format: "TicketID: {id} from {source} to {dest} on {date}"
```

---

### 4️⃣ Search Trains Flow

```
START: User selects option 4
    ↓
Enter source station
    ↓
Enter destination station
    ↓
Load trains.json
    ↓
Filter trains by route:
  - source exists in stations
  - destination exists in stations
  - source index < destination index
    ↓
Found trains?
    ├─ Yes ──────────────────┐
    │                        │
    ↓                        ↓ No
Display trains list      Display error
  - Train ID            "No trains found"
  - Train Number            ↓
  - Stations & times    Return to menu
    ↓
Prompt: "Select train (1,2,3...)"
    ↓
Enter selection
    ↓
Validate index (0 to size-1)
    ↓
    ├─ Valid ────────────┐
    │                    │
    ↓                    ↓ Invalid
Store in             Display error
trainSelectedForBooking  "Invalid selection"
    ↓                    ↓
Display confirmation  Return to menu
    ↓
Return to menu
```

**Train Search Algorithm:**
```
trains.json → List<Train>
    ↓
For each train:
    ↓
Get train.getStations() → ["A", "B", "C", "D"]
    ↓
Find indexOf(source) → e.g., 1 (B)
Find indexOf(destination) → e.g., 3 (D)
    ↓
Valid if: sourIndex != -1 AND destIndex != -1 AND sourIndex < destIndex
    ↓
    ├─ Valid → Add to results
    │
    ├─ Invalid → Skip
    ↓
Return filtered List<Train>
```

---

### 5️⃣ Book Seat Flow

```
START: User selects option 5
    ↓
Check if logged in
    ↓
    ├─ No ────────────────────┐
    │                         │
    ↓                         ↓
Check train selected      Display error
    ↓                     "Please login"
    ├─ No ────────────┐       ↓
    │                 │   Return to menu
    ↓                 ↓
Display seat matrix  Display error
(0=available,       "Please select train"
 1=booked)               ↓
    ↓               Return to menu
Enter row number
    ↓
Enter column number
    ↓
Enter source station
    ↓
Enter destination station
    ↓
Validate row & column
    ↓
    ├─ Valid ──────────────┐
    │                      │
    ↓                      ↓ Invalid
Check seat available   Display error
    ↓                  "Invalid seat"
    ├─ Available ───┐      ↓
    │               │  Return to menu
    ↓               ↓ Booked
Mark seat as true  Display error
    ↓              "Already booked"
Create Ticket:         ↓
  - Generate UUID  Return to menu
  - User ID
  - Source/Dest
  - Current DateTime
  - Train object
    ↓
Add to user.TicketBooking
    ↓
Update userList
    ↓
Save to users.json
    ↓
Display "Booked! Enjoy your journey"
    ↓
Return to menu
```

**Booking Transaction:**
```
trainSelectedForBooking
    ↓
Get train.getSeats() → [[false, false, ...], ...]
    ↓
seats[row][col] == false? (available)
    ↓
    ├─ Yes → Proceed
    │
    ├─ No → Return false
    ↓
Update: seats[row][col] = true
    ↓
Create new Ticket(
    ticketId: UUID.randomUUID(),
    userId: user.getUserId(),
    source: input,
    destination: input,
    dateTravel: LocalDateTime.now(),
    train: trainSelectedForBooking
)
    ↓
user.getTicketBooking().add(ticket)
    ↓
Update user in userList (find by userId)
    ↓
ObjectMapper.writeValue(users.json, userList)
    ↓
Return true (success)
```

---

### 6️⃣ Cancel Booking Flow

```
START: User selects option 6
    ↓
Check if logged in
    ↓
    ├─ No ──────────────┐
    │                   │
    ↓                   ↓
Enter Ticket ID     Display error
    ↓               "Please login"
Search in               ↓
user.TicketBooking  Return to menu
    ↓
Found ticket?
    ├─ Yes ─────────────┐
    │                   │
    ↓                   ↓ No
Remove from list    Display error
    ↓               "Ticket not found"
Update userList         ↓
    ↓               Return to menu
Save to users.json
    ↓
Display "Cancelled successfully"
    ↓
Return to menu
```

**Cancellation Process:**
```
Input: ticketId (string)
    ↓
Loop through user.getTicketBooking()
    ↓
For each ticket:
    ticket.getTicketId().equals(input)?
        ↓
        ├─ Yes → Remove at index i
        │        ↓
        │     Update user in userList
        │        ↓
        │     Save to users.json
        │        ↓
        │     Return true
        │
        ├─ No → Continue loop
    ↓
Loop complete, not found → Return false
```

---

### 7️⃣ Exit Flow

```
START: User selects option 7
    ↓
Break while loop
    ↓
Display "Thank you for using IRCTC Ticket Booking App!"
    ↓
Close Scanner
    ↓
END APPLICATION
```

---

## 🗂️ Data Persistence Workflow

### Write Operations

```
Java Object Changes
    ↓
Update in-memory userList/trainList
    ↓
Call ObjectMapper.writeValue(file, list)
    ↓
Jackson serializes:
  - @JsonProperty maps fields
  - Nested objects converted
    ↓
Write to JSON file
    ↓
File persisted to disk
```

### Read Operations

```
Application Start / Load Data
    ↓
Create File object (users.json / trains.json)
    ↓
Call ObjectMapper.readValue(file, TypeReference<List<T>>)
    ↓
Jackson deserializes:
  - @JsonProperty maps JSON → fields
  - @JsonIgnoreProperties skips unknown fields
  - Nested objects reconstructed
    ↓
Return List<User> or List<Train>
    ↓
Store in memory
```

---

## 🏗️ System Architecture Workflow

```
┌─────────────────────────────────────────────────┐
│                   App.java                      │
│              (Main Controller)                  │
│  - Scanner input                                │
│  - Menu loop                                    │
│  - User session management                      │
└──────────┬──────────────────────────────────────┘
           │
           ├──────────────────┬──────────────────┐
           │                  │                  │
           ▼                  ▼                  ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────┐
│ UserBookingService│ │ TrainService │ │userServiceUtil│
│  - Login         │ │ - Load trains│ │ - hashPassword│
│  - Signup        │ │ - Search     │ │ - checkPassword│
│  - Bookings      │ │ - Validate   │ └──────────────┘
│  - Cancel        │ └──────────────┘
│  - Book seat     │         │
└──────────────────┘         │
           │                 │
           ▼                 ▼
┌──────────────────┐ ┌──────────────┐
│   users.json     │ │ trains.json  │
│  - User list     │ │ - Train list │
│  - Tickets       │ │ - Seats      │
│  - Hashed pwd    │ │ - Stations   │
└──────────────────┘ └──────────────┘
```

---

## 🔐 Security Workflow

### Password Hashing (Signup)

```
User enters password: "mypass123"
    ↓
userServiceUtil.hashPassword(password)
    ↓
BCrypt.hashpw("mypass123", BCrypt.gensalt())
    ↓
Generate salt (random)
    ↓
Hash with Blowfish algorithm
    ↓
Result: "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
    ↓
Store in User.hashPassword field
    ↓
Save to users.json
```

### Password Verification (Login)

```
User enters: "mypass123"
    ↓
Retrieve stored hash from users.json
    ↓
userServiceUtil.checkPassword(input, storedHash)
    ↓
BCrypt.checkpw("mypass123", "$2a$10$N9q...")
    ↓
BCrypt re-hashes input with same salt
    ↓
Compare hashes
    ↓
    ├─ Match → Return true → Login success
    │
    ├─ No match → Return false → Login failed
```

---

## 📊 State Management Workflow

### Session State

```
Application starts
    ↓
userBookingService = null (no session)
trainSelectedForBooking = null
    ↓
User logs in (option 2)
    ↓
userBookingService = new UserBookingService(user)
    ↓
    ├─ Success → userBookingService.user = authenticated user
    │            ↓
    │         Session active (user != null)
    │            ↓
    │         Can access: Fetch, Book, Cancel
    │
    ├─ Failure → userBookingService.user = null
                 ↓
              No session (restricted access)
                 ↓
              Must login first
```

### Train Selection State

```
Initially: trainSelectedForBooking = null
    ↓
User searches trains (option 4)
    ↓
Display results
    ↓
User selects train
    ↓
trainSelectedForBooking = trains.get(index)
    ↓
State persists until:
  - New search
  - Application exit
    ↓
Required for option 5 (Book seat)
```

---

## 🔄 Complete Application Lifecycle

```
┌─────────────────────────────────────────────┐
│ 1. APPLICATION START                        │
│    - Initialize Scanner                     │
│    - Load UserBookingService                │
│    - Load users.json                        │
└────────────┬────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────┐
│ 2. MAIN LOOP (while option != 7)           │
│    - Display menu                           │
│    - Read user input                        │
│    - Execute selected operation             │
└────────────┬────────────────────────────────┘
             │
             ├─── option 1: Sign Up
             ├─── option 2: Login
             ├─── option 3: Fetch Bookings
             ├─── option 4: Search Trains
             ├─── option 5: Book Seat
             ├─── option 6: Cancel Booking
             │
             ▼
┌─────────────────────────────────────────────┐
│ 3. OPERATION EXECUTION                      │
│    - Validate preconditions                 │
│    - Process business logic                 │
│    - Update data if needed                  │
│    - Display result                         │
└────────────┬────────────────────────────────┘
             │
             ▼ (loop continues)
             │
             ▼ option 7
┌─────────────────────────────────────────────┐
│ 4. APPLICATION EXIT                         │
│    - Display thank you message              │
│    - Close Scanner                          │
│    - Terminate                              │
└─────────────────────────────────────────────┘
```

---

## 🧩 Component Interaction Workflow

### Example: Complete Booking Flow

```
1. User Input Layer (App.java)
   User selects: 2 (Login)
        ↓
   Enter: "john", "pass123"
        ↓
   
2. Service Layer (UserBookingService)
   Create User object with input
        ↓
   Call loginUser(userObj)
        ↓
   
3. Utility Layer (userServiceUtil)
   BCrypt.checkpw("pass123", stored_hash)
        ↓
   Return: true/false
        ↓
   
4. Data Layer (users.json)
   Read file → Deserialize → List<User>
        ↓
   
5. Back to Service
   Match found? → Set session
        ↓
   
6. Back to App
   Display: "Login successful!"
        ↓
   
7. User Input: 4 (Search Trains)
        ↓
   Enter: "bangalore", "delhi"
        ↓
   
8. Service Layer (TrainService)
   Load trains.json
        ↓
   Filter by route
        ↓
   Return matching trains
        ↓
   
9. App Layer
   Display results
        ↓
   User selects train #1
        ↓
   Store in trainSelectedForBooking
        ↓
   
10. User Input: 5 (Book Seat)
        ↓
   Display seat matrix
        ↓
   User enters: row=0, col=2
        ↓
   
11. Service Layer (UserBookingService)
   Validate seat available
        ↓
   Mark seat as booked
        ↓
   Create Ticket object
        ↓
   Add to user.TicketBooking
        ↓
   
12. Data Layer
   Update users.json
        ↓
   Serialize & write
        ↓
   
13. Back to App
   Display: "Booked! Enjoy your journey"
        ↓
   Return to menu
```

---

## ⚠️ Error Handling Workflow

```
Any Operation
    ↓
Try-Catch Block
    ↓
    ├─ Success → Proceed normally
    │            ↓
    │         Display success message
    │
    ├─ IOException → Catch
    │                ↓
    │             Display: "Operation failed: {message}"
    │                ↓
    │             Return to menu
    │
    ├─ Validation Failure → Check before processing
                            ↓
                         Display: "Invalid input"
                            ↓
                         Return to menu
```

### Validation Points

```
1. Input Validation
   - Empty strings
   - Null values
   - Out of range indices
   
2. Authentication Check
   - User logged in?
   - Valid credentials?
   
3. State Validation
   - Train selected?
   - Seat available?
   - Ticket exists?
   
4. Data Integrity
   - File exists?
   - Valid JSON?
   - Correct format?
```

---

## 🎯 Key Workflows Summary

1. **Authentication**: Password hashing → Storage → Verification
2. **Booking**: Search → Select → Validate → Reserve → Persist
3. **Data Persistence**: In-memory ↔ JSON (Jackson)
4. **Session Management**: Login → Set user → Logout/Exit
5. **Error Handling**: Validate → Try-Catch → User feedback

This workflow ensures secure, reliable ticket booking with proper data management and user experience.
