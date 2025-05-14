# Cinema Mobile App Development Specification

## 1. App Overview
Create a modern, user-friendly cinema ticket booking mobile application using Java for Android. The app should provide seamless movie booking experience with real-time seat selection, secure payments, and push notifications.

## 2. Technical Stack
- Language: Java
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)
- Architecture: MVVM (Model-View-ViewModel)
- Libraries:
  - Retrofit for API calls
  - OkHttp for networking
  - Room for local caching
  - LiveData for reactive UI
  - ViewModel for UI state management
  - Navigation Component for navigation
  - Glide for image loading
  - Dagger Hilt for dependency injection
  - DataStore for preferences
  - WorkManager for background tasks
  - Firebase Cloud Messaging for push notifications

## 3. Core Features

### 3.1 Authentication Module
- Login Screen:
  - Phone number input with country code
  - Password input
  - "Remember me" option
  - Forgot password link
  - Sign up link
  - Social login options (Google, Facebook)
  - Biometric authentication option

- Sign Up Screen:
  - Full name input
  - Phone number input
  - Email input (optional)
  - Password creation
  - Terms and conditions acceptance
  - Profile picture upload (optional)

- Password Reset Screen:
  - Phone number input
  - OTP verification
  - New password creation

### 3.2 Home Module
- Bottom Navigation Bar:
  - Home
  - Movies
  - Bookings
  - Profile

- Home Screen:
  - Featured movies carousel
  - Now showing section
  - Coming soon section
  - Special offers banner
  - Quick booking section
  - Location-based cinema suggestions

- Movie List Screen:
  - Search bar
  - Filter options:
    - Genre
    - Language
    - Rating
    - Release date
  - Sort options:
    - Popularity
    - Rating
    - Release date
  - Grid/List view toggle
  - Pull to refresh
  - Infinite scroll

### 3.3 Movie Details Module
- Movie Details Screen:
  - Movie poster
  - Title and release year
  - Rating and duration
  - Genre tags
  - Synopsis
  - Cast and crew
  - Trailer video
  - User reviews
  - Similar movies
  - Book now button

- Showtime Selection Screen:
  - Date picker
  - Cinema selection
  - Showtime list
  - Screen type (2D/3D/IMAX)
  - Price display

### 3.4 Booking Module
- Seat Selection Screen:
  - Interactive seat layout
  - Seat categories (Standard, Premium, VIP)
  - Price per seat
  - Selected seats summary
  - Screen view
  - Legend for seat types
  - Maximum seats selection limit

- Booking Summary Screen:
  - Movie details
  - Showtime details
  - Selected seats
  - Subtotal
  - Convenience fee
  - Total amount
  - Promo code input
  - Payment method selection

- Payment Screen:
  - Payment method options:
    - Credit/Debit card
    - UPI
    - Net banking
    - Wallet
  - Card details input
  - Save card option
  - Payment confirmation

- Booking Confirmation Screen:
  - Booking ID
  - QR code
  - Movie details
  - Showtime details
  - Seat numbers
  - Payment details
  - Share booking option
  - Add to calendar option

### 3.5 Bookings History Module
- Bookings List Screen:
  - Active bookings
  - Past bookings
  - Cancelled bookings
  - Filter by status
  - Search by booking ID
  - Sort by date

- Booking Details Screen:
  - Booking status
  - Movie details
  - Showtime details
  - Seat numbers
  - Payment details
  - Cancel booking option
  - Reschedule option
  - Download ticket option

### 3.6 Profile Module
- Profile Screen:
  - Profile picture
  - Personal information
  - Edit profile option
  - Settings
  - Help & Support
  - Logout option

- Settings Screen:
  - Notification preferences
  - Language selection
  - Theme selection
  - Payment methods
  - Privacy settings
  - About app

## 4. UI/UX Guidelines

### 4.1 Design System
- Color Palette:
  - Primary: #1A237E (Deep Blue)
  - Secondary: #FF4081 (Pink)
  - Background: #FFFFFF (White)
  - Text: #212121 (Dark Gray)
  - Accent: #FFC107 (Amber)

- Typography:
  - Headings: Roboto Bold
  - Body: Roboto Regular
  - Buttons: Roboto Medium

- Components:
  - Material Design components
  - Custom card designs
  - Custom button styles
  - Custom input fields
  - Custom dialogs

### 4.2 Animations
The following animations provide a consistent and engaging user experience. These specifications should be used as a guideline when implementing animations.

#### 4.2.1 Screen Transitions

##### Standard Navigation Transitions
```json
// Base transition duration: 300ms
// Easing: FastOutSlowInInterpolator

// Forward Navigation (Push)
{
    "type": "slide",
    "direction": "right",
    "duration": 300,
    "interpolator": "FastOutSlowInInterpolator",
    "enterAnimation": {
        "translationX": ["100%", "0%"],
        "alpha": [0, 1]
    },
    "exitAnimation": {
        "translationX": ["0%", "-20%"],
        "alpha": [1, 0.5]
    }
}

// Back Navigation (Pop)
{
    "type": "slide",
    "direction": "left",
    "duration": 300,
    "interpolator": "FastOutSlowInInterpolator",
    "enterAnimation": {
        "translationX": ["-20%", "0%"],
        "alpha": [0.5, 1]
    },
    "exitAnimation": {
        "translationX": ["0%", "100%"],
        "alpha": [1, 0]
    }
}
```

##### Modal Transitions
```json
// Modal Present
{
    "type": "fade",
    "duration": 250,
    "interpolator": "FastOutSlowInInterpolator",
    "enterAnimation": {
        "scale": [0.9, 1.0],
        "alpha": [0, 1]
    },
    "backgroundAnimation": {
        "alpha": [0, 0.5]
    }
}

// Modal Dismiss
{
    "type": "fade",
    "duration": 200,
    "interpolator": "FastOutSlowInInterpolator",
    "exitAnimation": {
        "scale": [1.0, 0.9],
        "alpha": [1, 0]
    },
    "backgroundAnimation": {
        "alpha": [0.5, 0]
    }
}
```

#### 4.2.2 Component Animations

##### Button Animations
```json
// Primary Button Press
{
    "type": "scale",
    "duration": 150,
    "interpolator": "FastOutSlowInInterpolator",
    "pressAnimation": {
        "scale": [1.0, 0.95],
        "alpha": [1.0, 0.9]
    },
    "releaseAnimation": {
        "scale": [0.95, 1.0],
        "alpha": [0.9, 1.0]
    }
}

// Secondary Button Press
{
    "type": "ripple",
    "duration": 300,
    "rippleColor": "#1A237E",
    "rippleAlpha": 0.1
}
```

##### Card Animations
```json
// Card Enter
{
    "type": "fade",
    "duration": 400,
    "interpolator": "FastOutSlowInInterpolator",
    "staggerDelay": 50,
    "enterAnimation": {
        "translationY": ["20dp", "0dp"],
        "alpha": [0, 1]
    }
}

// Card Press
{
    "type": "scale",
    "duration": 200,
    "interpolator": "FastOutSlowInInterpolator",
    "pressAnimation": {
        "scale": [1.0, 0.98],
        "elevation": ["4dp", "2dp"] // Note: dp values for elevation
    }
}
```

#### 4.2.3 List Animations

##### RecyclerView Animations
```json
// List Item Enter
{
    "type": "slide",
    "duration": 300,
    "interpolator": "FastOutSlowInInterpolator",
    "staggerDelay": 50,
    "enterAnimation": {
        "translationX": ["100%", "0%"],
        "alpha": [0, 1]
    }
}

// List Item Remove
{
    "type": "fade",
    "duration": 200,
    "interpolator": "FastOutSlowInInterpolator",
    "exitAnimation": {
        "translationX": ["0%", "-100%"],
        "alpha": [1, 0]
    }
}
```

#### 4.2.4 Loading Animations

##### Progress Indicators
```json
// Circular Progress
{
    "type": "rotate",
    "duration": 1000,
    "interpolator": "LinearInterpolator",
    "rotation": [0, 360],
    "strokeWidth": "2dp",
    "strokeColor": "#1A237E"
}

// Skeleton Loading
{
    "type": "shimmer",
    "duration": 1000,
    "interpolator": "LinearInterpolator",
    "shimmerColor": "#E0E0E0",
    "shimmerAlpha": [0.3, 0.7, 0.3]
}
```

#### 4.2.5 Special Animations

##### Seat Selection Animation
```json
// Seat Select
{
    "type": "scale",
    "duration": 200,
    "interpolator": "FastOutSlowInInterpolator",
    "selectAnimation": {
        "scale": [1.0, 1.2, 1.0],
        "rotation": [0, 10, -10, 0]
    }
}

// Seat Deselect
{
    "type": "scale",
    "duration": 200,
    "interpolator": "FastOutSlowInInterpolator",
    "deselectAnimation": {
        "scale": [1.0, 0.8, 1.0]
    }
}
```

##### Payment Processing Animation
```json
// Payment Success
{
    "type": "sequence",
    "duration": 1000,
    "animations": [
        {
            "type": "scale",
            "duration": 300,
            "scale": [0, 1.2, 1]
        },
        {
            "type": "rotate",
            "duration": 300,
            "rotation": [0, 360]
        }
    ]
}

// Payment Error
{
    "type": "shake",
    "duration": 500,
    "interpolator": "FastOutSlowInInterpolator",
    "shakeAnimation": {
        "translationX": ["0%", "-10%", "10%", "-10%", "10%", "0%"]
    }
}
```

#### 4.2.6 Implementation Guidelines

##### Animation Constants
```java
// Example: public class AnimationConstants 
// (Actual implementation might vary based on project structure)
public class AnimationConstants {
    // Durations
    public static final long SHORT_DURATION = 150;
    public static final long MEDIUM_DURATION = 300;
    public static final long LONG_DURATION = 500;
    
    // Interpolators (Assuming Android interpolators)
    // import android.view.animation.FastOutSlowInInterpolator;
    // import android.view.animation.LinearInterpolator;
    // public static final Interpolator FAST_OUT_SLOW_IN = 
    //     new FastOutSlowInInterpolator();
    // public static final Interpolator LINEAR = 
    //     new LinearInterpolator();
    
    // Animation Types (Conceptual, for use in utility classes if needed)
    public static final int TYPE_SLIDE = 0;
    public static final int TYPE_FADE = 1;
    public static final int TYPE_SCALE = 2;
    public static final int TYPE_ROTATE = 3;
}
```

##### Animation Utility Class
```java
// Example: public class AnimationUtils 
// (Actual implementation might vary based on project structure and animation libraries used)

// Define an AnimationSpec class or use a Map/Bundle to pass parameters
// public class AnimationSpec { ... } 

public class AnimationUtils {
    // public static void animateView(View view, AnimationSpec spec) {
    //     // Implementation for applying animations based on spec
    //     // e.g., view.animate().translationX(...).setDuration(...).start();
    // }
    
    // public static void animateListItems(RecyclerView recyclerView, 
    //                                   AnimationSpec spec) {
    //     // Implementation for list animations
    //     // e.g., using LayoutAnimationController or custom ItemAnimator
    // }
    
    // public static void animateSeatSelection(View seatView, 
    //                                       boolean isSelected) {
    //     // Implementation for seat selection
    //     // e.g., using ViewPropertyAnimator or ObjectAnimator
    // }
}
```

##### Usage Example
```java
// Example: In an Activity or Fragment
// private void setupAnimations() {
    // Screen transition (Android specific)
    // overridePendingTransition(
    //     R.anim.slide_in_right, // Defined in res/anim
    //     R.anim.slide_out_left  // Defined in res/anim
    // );
    
    // List animation example (conceptual)
    // AnimationSpec listSpec = new AnimationSpec.Builder()
    //     .setType(AnimationConstants.TYPE_SLIDE)
    //     .setDuration(AnimationConstants.MEDIUM_DURATION)
    //     // .setInterpolator(AnimationConstants.FAST_OUT_SLOW_IN) // Assuming interpolator is an object
    //     .build();
    // AnimationUtils.animateListItems(recyclerView, listSpec);
    
    // Button animation example (conceptual)
    // Assuming pressSpec and releaseSpec are configured AnimationSpec objects
    // button.setOnTouchListener((v, event) -> {
    //     switch (event.getAction()) {
    //         case MotionEvent.ACTION_DOWN:
    //             AnimationUtils.animateView(v, pressSpec);
    //             break;
    //         case MotionEvent.ACTION_UP:
    //         case MotionEvent.ACTION_CANCEL:
    //             AnimationUtils.animateView(v, releaseSpec);
    //             break;
    //     }
    //     return false; // Or true if the event is consumed
    // });
// }
```

The previously listed general animation types (Screen transitions, Loading animations, etc.) still apply and should use these detailed specifications.

### 4.3 Responsive Design
- Support for different screen sizes
- Landscape mode support
- Tablet optimization
- Dynamic text sizing

### 4.4 Screen Mockups (Text-based)

These mockups provide a visual guide for the layout and components of each screen.

#### 4.4.1 Authentication Module

##### Login Screen

```
+----------------------------------+
|            [Logo]                |
|        Cinema Booking App        |
+----------------------------------+
|                                  |
|  [Phone Number Input]            |
|  +------------------------+      |
|  | +91 | 9876543210       |      |
|  +------------------------+      |
|                                  |
|  [Password Input]                |
|  +------------------------+      |
|  | ••••••••               |      |
|  +------------------------+      |
|                                  |
|  [ ] Remember Me                 |
|  [Forgot Password?]              |
|                                  |
|  [Sign In Button]                |
|  +------------------------+      |
|  |     SIGN IN            |      |
|  +------------------------+      |
|                                  |
|  [Or continue with]              |
|                                  |
|  [Google] [Facebook]             |
|                                  |
|  Don't have an account?          |
|  [Sign Up]                       |
+----------------------------------+
```

##### Sign Up Screen

```
+----------------------------------+
|            [Logo]                |
|        Create Account            |
+----------------------------------+
|                                  |
|  [Profile Picture Upload]        |
|  +------------------------+      |
|  |     [Camera Icon]      |      |
|  |    Add Photo           |      |
|  +------------------------+      |
|                                  |
|  [Full Name Input]               |
|  +------------------------+      |
|  | John Doe               |      |
|  +------------------------+      |
|                                  |
|  [Phone Number Input]            |
|  +------------------------+      |
|  | +91 | 9876543210       |      |
|  +------------------------+      |
|                                  |
|  [Email Input]                   |
|  +------------------------+      |
|  | john@example.com       |      |
|  +------------------------+      |
|                                  |
|  [Password Input]                |
|  +------------------------+      |
|  | ••••••••               |      |
|  +------------------------+      |
|                                  |
|  [ ] I agree to Terms &          |
|      Conditions                  |
|                                  |
|  [Create Account Button]         |
|  +------------------------+      |
|  |   CREATE ACCOUNT       |      |
|  +------------------------+      |
|                                  |
|  Already have an account?        |
|  [Sign In]                       |
+----------------------------------+
```

#### 4.4.2 Home Module

##### Home Screen

```
+----------------------------------+
| [Search] [Notifications] [Cart]  |
+----------------------------------+
|                                  |
|  [Featured Movies Carousel]      |
|  +------------------------+      |
|  | [Movie Poster 1]       |      |
|  | [Movie Poster 2]       |      |
|  | [Movie Poster 3]       |      |
|  +------------------------+      |
|                                  |
|  Now Showing                     |
|  +------------------------+      |
|  | [Movie Card 1]         |      |
|  | [Movie Card 2]         |      |
|  | [Movie Card 3]         |      |
|  +------------------------+      |
|                                  |
|  Coming Soon                     |
|  +------------------------+      |
|  | [Movie Card 1]         |      |
|  | [Movie Card 2]         |      |
|  | [Movie Card 3]         |      |
|  +------------------------+      |
|                                  |
|  [Special Offers Banner]         |
|  +------------------------+      |
|  | 50% OFF on Weekdays    |      |
|  +------------------------+      |
|                                  |
|  [Bottom Navigation Bar]         |
|  [Home] [Movies] [Bookings] [Profile]
+----------------------------------+
```

##### Movie List Screen

```
+----------------------------------+
| [Search Bar] [Filter] [Sort]     |
+----------------------------------+
|                                  |
|  [Filter Chips]                  |
|  [Action] [Comedy] [Drama]       |
|                                  |
|  [Movie Grid/List View]          |
|  +--------+  +--------+          |
|  |[Poster]|  |[Poster]|          |
|  |Title   |  |Title   |          |
|  |Rating  |  |Rating  |          |
|  +--------+  +--------+          |
|  +--------+  +--------+          |
|  |[Poster]|  |[Poster]|          |
|  |Title   |  |Title   |          |
|  |Rating  |  |Rating  |          |
|  +--------+  +--------+          |
|                                  |
|  [Load More]                     |
|                                  |
|  [Bottom Navigation Bar]         |
|  [Home] [Movies] [Bookings] [Profile]
+----------------------------------+
```

#### 4.4.3 Movie Details Module

##### Movie Details Screen

```
+----------------------------------+
| [Back] [Share] [Favorite]        |
+----------------------------------+
|                                  |
|  [Movie Poster]                  |
|  +------------------------+      |
|  |                        |      |
|  |                        |      |
|  +------------------------+      |
|                                  |
|  Movie Title (2024)              |
|  ⭐ 4.5 | 2h 30m | Action        |
|                                  |
|  [Book Now Button]               |
|  +------------------------+      |
|  |     BOOK NOW           |      |
|  +------------------------+      |
|                                  |
|  Synopsis                        |
|  [Movie description text...]     |
|                                  |
|  Cast & Crew                     |
|  [Horizontal Scroll]             |
|  [Actor 1] [Actor 2] [Actor 3]   |
|                                  |
|  Trailer                         |
|  [Video Thumbnail]               |
|                                  |
|  Reviews                         |
|  [Review 1]                      |
|  [Review 2]                      |
|                                  |
|  Similar Movies                  |
|  [Horizontal Scroll]             |
|  [Movie 1] [Movie 2] [Movie 3]   |
+----------------------------------+
```

##### Showtime Selection Screen

```
+----------------------------------+
| [Back] Movie Title               |
+----------------------------------+
|                                  |
|  [Date Picker]                   |
|  [Mon] [Tue] [Wed] [Thu] [Fri]   |
|                                  |
|  [Cinema Selection]              |
|  +------------------------+      |
|  | [Cinema 1]             |      |
|  | [Cinema 2]             |      |
|  | [Cinema 3]             |      |
|  +------------------------+      |
|                                  |
|  [Showtime List]                 |
|  +------------------------+      |
|  | 10:00 AM | 2D | $10    |      |
|  | 01:00 PM | 3D | $15    |      |
|  | 04:00 PM | IMAX| $20   |      |
|  +------------------------+      |
|                                  |
|  [Screen Type Selection]         |
|  [2D] [3D] [IMAX]                |
|                                  |
|  [Continue Button]               |
|  +------------------------+      |
|  |    CONTINUE            |      |
|  +------------------------+      |
+----------------------------------+
```

#### 4.4.4 Booking Module

##### Seat Selection Screen

```
+----------------------------------+
| [Back] Select Seats              |
+----------------------------------+
|                                  |
|  [Screen View]                   |
|  +------------------------+      |
|  |        SCREEN          |      |
|  +------------------------+      |
|                                  |
|  [Seat Layout]                   |
|  +------------------------+      |
|  | [A1][A2][A3][A4][A5]   |      |
|  | [B1][B2][B3][B4][B5]   |      |
|  | [C1][C2][C3][C4][C5]   |      |
|  | [D1][D2][D3][D4][D5]   |      |
|  +------------------------+      |
|                                  |
|  [Seat Legend]                   |
|  [Available] [Selected] [Booked] |
|                                  |
|  [Selected Seats Summary]        |
|  +------------------------+      |
|  | A3, A4, A5             |      |
|  | Total: $45             |      |
|  +------------------------+      |
|                                  |
|  [Continue Button]               |
|  +------------------------+      |
|  |    CONTINUE            |      |
|  +------------------------+      |
+----------------------------------+
```

##### Booking Summary Screen

```
+----------------------------------+
| [Back] Booking Summary           |
+----------------------------------+
|                                  |
|  [Movie Details]                 |
|  +------------------------+      |
|  | Movie Title            |      |
|  | Showtime: 10:00 AM     |      |
|  | Seats: A3, A4, A5      |      |
|  +------------------------+      |
|                                  |
|  [Price Breakdown]               |
|  +------------------------+      |
|  | Subtotal: $45          |      |
|  | Convenience Fee: $5    |      |
|  | Total: $50             |      |
|  +------------------------+      |
|                                  |
|  [Promo Code]                    |
|  +------------------------+      |
|  | [Input] [Apply]        |      |
|  +------------------------+      |
|                                  |
|  [Payment Method]                |
|  +------------------------+      |
|  | [ ] Credit Card        |      |
|  | [ ] UPI                |      |
|  | [ ] Net Banking        |      |
|  +------------------------+      |
|                                  |
|  [Proceed to Pay Button]         |
|  +------------------------+      |
|  |   PROCEED TO PAY       |      |
|  +------------------------+      |
+----------------------------------+
```

#### 4.4.5 Bookings History Module

##### Bookings List Screen

```
+----------------------------------+
| [Back] My Bookings               |
+----------------------------------+
|                                  |
|  [Filter Tabs]                   |
|  [Active] [Past] [Cancelled]     |
|                                  |
|  [Search Bookings]               |
|  +------------------------+      |
|  | Search by booking ID   |      |
|  +------------------------+      |
|                                  |
|  [Booking Cards]                 |
|  +------------------------+      |
|  | Movie Title            |      |
|  | Date: 15 May 2024      |      |
|  | Time: 10:00 AM         |      |
|  | Status: Confirmed      |      |
|  +------------------------+      |
|                                  |
|  [Sort Options]                  |
|  [Date] [Status] [Amount]        |
|                                  |
|  [Bottom Navigation Bar]         |
|  [Home] [Movies] [Bookings] [Profile]
+----------------------------------+
```

#### 4.4.6 Profile Module

##### Profile Screen

```
+----------------------------------+
| [Settings] [Edit]                |
+----------------------------------+
|                                  |
|  [Profile Picture]               |
|  +------------------------+      |
|  |     [User Photo]       |      |
|  +------------------------+      |
|                                  |
|  [User Name]                     |
|  [Phone Number]                  |
|  [Email Address]                 |
|                                  |
|  [Menu Items]                    |
|  +------------------------+      |
|  | [Icon] My Bookings     |      |
|  | [Icon] Payment Methods |      |
|  | [Icon] Notifications   |      |
|  | [Icon] Help & Support  |      |
|  | [Icon] Settings        |      |
|  | [Icon] Logout          |      |
|  +------------------------+      |
|                                  |
|  [Bottom Navigation Bar]         |
|  [Home] [Movies] [Bookings] [Profile]
+----------------------------------+
```

## 5. API Integration

### 5.1 API Endpoints
- Authentication:
  - POST /api/auth/signup
  - POST /api/auth/login
  - POST /api/auth/refresh
  - POST /api/auth/logout

- Movies:
  - GET /api/movies
  - GET /api/movies/{id}
  - GET /api/movies/cinema/{cinemaId}
  - GET /api/movies/search

- Showtimes:
  - GET /api/showtimes
  - GET /api/showtimes/{id}
  - GET /api/showtimes/movie/{movieId}

- Bookings:
  - POST /api/bookings
  - GET /api/bookings/{bookingId}
  - GET /api/bookings/user
  - POST /api/bookings/{bookingId}/cancel

- Seats:
  - GET /api/seats/showtime/{showtimeId}

- Notifications:
  - GET /api/notifications/user
  - POST /api/notifications/{id}/read

### 5.2 Data Models
- Request/Response models for each API
- Error handling models
- Local database models
- View models

## 6. Offline Support
- Local caching of:
  - Movie data
  - User profile
  - Booking history
  - Seat layouts
- Offline booking queue
- Sync when online

## 7. Security Features
- SSL pinning
- Biometric authentication
- Secure storage of sensitive data
- Token management
- Session handling
- Input validation
- XSS prevention

## 8. Performance Optimization
- Image caching
- Lazy loading
- Pagination
- Background data prefetching
- Memory management
- Battery optimization

## 9. Testing Requirements
- Unit tests
- Integration tests
- UI tests
- Performance tests
- Security tests

## 10. Analytics Integration
- User behavior tracking
- Error tracking
- Performance monitoring
- Crash reporting
- User engagement metrics

## 11. Deployment
- Google Play Store release
- Beta testing program
- Staged rollout
- A/B testing capability
- Update mechanism

## 12. Maintenance
- Error logging
- Performance monitoring
- User feedback system
- Regular updates
- Backup and recovery