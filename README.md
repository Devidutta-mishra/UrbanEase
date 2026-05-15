# UrbanEase 🏠

UrbanEase is a modern, real-time property management and discovery platform designed for the urban lifestyle. Built with **Jetpack Compose** and **Firebase**, it provides a seamless experience for Bachelors to find homes and for Owners to manage their listings.

## ✨ Features

### 🏢 For Bachelors
- **Modern UI**: Intuitive search and discovery interface inspired by high-end real estate apps.
- **Real-time Listings**: Instant updates on approved properties.
- **Smart Filters**: Filter properties by Location, Rent Range, and Room count.
- **Detailed View**: High-quality image support and comprehensive property overviews.
- **Booking Requests**: Send booking interests directly to owners with a single click.

### 🔑 For Owners
- **Dashboard Overview**: Track active listings at a glance.
- **Property Management**: Effortlessly list new properties with image uploads to Firebase Storage.
- **Live Status Tracking**: Monitor the approval status (Approved, Pending, Rejected) of your listings in real-time.
- **Interactive Management**: Accept or reject incoming booking requests from bachelors.

### 🛡️ For Admins
- **Approval System**: Review and verify all new property listings before they go live.
- **User Oversight**: View and manage the community of owners and bachelors.
- **Revoke/Approve**: Full control over property visibility to ensure a safe marketplace.

## 🛠️ Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% Kotlin)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Backend**: [Firebase](https://firebase.google.com/)
    - **Authentication**: Email/Password login with role-based access.
    - **Firestore**: Real-time NoSQL database for ads, bookings, and users.
    - **Storage**: Image hosting for property photos.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

## ⚖️ License

Distributed under the MIT License. See `LICENSE` for more information.

---
*Created with ❤️ for modern urban living.*
