# Ayoba Images

A modern Android application that displays a beautiful gallery of cat images using The Cat API. Built with Kotlin and following MVVM architecture pattern.

## Features

- 🐱 Display cat images in a responsive grid layout
- 🔄 Pull-to-refresh functionality
- 📱 Infinite scrolling with pagination
- 💾 Offline support with local caching
- 🔍 Image details view
- 🌐 Network state handling
- 🎨 Modern Material Design UI
- 📱 Support for different screen sizes and orientations

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Libraries**:
  - [Hilt](https://dagger.dev/hilt/) - Dependency injection
  - [Retrofit](https://square.github.io/retrofit/) - Network calls
  - [Room](https://developer.android.com/training/data-storage/room) - Local database
  - [Glide](https://bumptech.github.io/glide/) - Image loading and caching
  - [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Asynchronous programming
  - [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) - Reactive streams
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - View binding
  - [SwipeRefreshLayout](https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout) - Pull-to-refresh

## Project Structure

```
app/
├── data/
│   ├── api/         # API interfaces and models
│   ├── db/          # Room database and DAOs
│   ├── model/       # Data models
│   └── repository/  # Repository implementations
├── di/              # Dependency injection modules
├── ui/
│   ├── adapter/     # RecyclerView adapters
│   ├── viewmodel/   # ViewModels
│   └── MainActivity # Main UI components
└── util/            # Utility classes
```


## UI/UX

  <tr>
    <td><img src="https://github.com/user-attachments/assets/8a0d6633-9858-4061-b905-ea3bf5867d39" width="40%"></td>
    <td><img src="https://github.com/user-attachments/assets/11b4df94-2f3c-49a2-a3b3-6c6439a644bf" width="40%"></td>
  </tr>

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- JDK 11 or newer
- Android SDK 21 or newer

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/AyobaImages.git
```

2. Open the project in Android Studio

3. Build and run the application

## API Key

The application uses The Cat API. You don't need an API key for basic functionality, but if you want to use advanced features, you can get an API key from [The Cat API](https://thecatapi.com/).

## Project Documentation

### Technical Overview
For a comprehensive technical overview of the project architecture, components, and implementation details, please refer to our [Technical Documentation](TECHNICAL_DOCUMENTATION.md). This document covers:
- Architecture patterns and design decisions
- Data layer implementation
- Domain layer structure
- Presentation layer components
- Error handling strategies
- Performance considerations
- Testing approach
- Security measures

### Implementation Guide
For detailed implementation instructions, code examples, and best practices, check out our [Implementation Details](IMPLEMENTATION_DETAILS.md) guide. This document provides:
- Step-by-step setup instructions
- Code snippets and examples
- UI implementation details
- Error handling patterns
- Performance optimization techniques
- Testing implementation
- Troubleshooting guides
- Maintenance procedures

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [The Cat API](https://thecatapi.com/) for providing the cat images
- [Android Jetpack](https://developer.android.com/jetpack) for the architecture components
- [Material Design](https://material.io/design) for the design guidelines 
