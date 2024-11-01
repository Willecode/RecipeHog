# RecipeHog

**RecipeHog** is an Android app where users can share and discover recipes.

https://github.com/user-attachments/assets/3203ad3c-0c68-4169-bdf1-509313a58e1d

## Features
- Register and sign in
- Create and share recipes
- Browse and search
- Like and bookmark recipes
- Post and read reviews
- Edit your own profile appearance
- Works offline with cached data when user has no internet connection

## Technical details
- Clean architecture
- MVVM
- Multi-module project with feature-based modules
- Offline-first approach
- Dependency Injection (Koin)
- Firebase backend (Authentication, Storage, Firestore database)
- UI with Jetpack Compose
- Utilizes device camera
- Utilizes Kotlin Coroutines and Flows
- Custom gradle plugins for easy build config management 

## Architecture
### Layering
The project has three layers, as in Clean Architecture:
- **Presentation**: Android library that implements the UI.
- **Domain**: Kotlin library that implements business logic and defines platform-independent interfaces and models. Also contains Use cases, which are commonly used pieces of business logic.
- **Data**: Android/Kotlin library that handles back-end requests and data caching.
- The layer dependencies are as follows: **Presentation -> Domain <- Data**, meaning that Presentation and Data depend on Domain.

### Unidirectional Data Flow (UDF)
State flows like this: **Data -> Domain -> Presentation**
- State flows from Data layer, through Domain layer, to the Presentation layer, which then uses the state to display info to the user.

Events flow like this: **Presentation -> Domain -> Data**
- Events are fired in the Presentation layer by calling Domain layer's services. The domain layer may then perform business logic, and call Data layer services.

### Dependency injection (DI)
The data flow between the layers is accomplished via DI. The Domain layer defines **interfaces**. The Presentation layer depends on these interfaces, and **calls** their abstract functions. The Data layer also depends on the Domain layer, because it **implements** the Domain layer's interfaces.

The Presentation layer's class constructors have Domain layer's interfaces as parameters, and in runtime, the objects are constructed with **Data layer's implementations of the interfaces**.

This is why the Data layer depends on the Domain layer and not vice versa. This is a form of [Dependency inversion](https://en.wikipedia.org/wiki/Dependency_inversion_principle).

### Layered, feature-based modules
There are four types of modules in the project: **feature, core, app, build-logic**.

**Feature modules:**
- The project has a couple of these: **auth, bookmarks, discover, home, profile, recipe, review**.
- Implements a feature. For example, the authentication feature is implemented by the "auth" module.
- Divided into Presentation, Domain, and Data layers.
- Depends on the core module.

**Core module:**
- All other modules (except build-logic) depend on it.
- Divided into Presentation, Domain, and Data layers.
- Feature modules' layers depend on the corresponding layer of the core module + the domain layer. i.e. auth:presentation can depend on core:presentation and core:domain. auth:domain can depend on core:domain. auth:data can't access core:presentation.
- Contains functionality that is used project-wide.

**App module:**
- Depends on all the other modules (except build-logic)
- Glues all the other modules together into an Android app.
- Implements navigation between screens

**build-logic:**
- Contains Gradle convention plugins that are used to configure build settings such as the target Android SDK, Kotlin version, build types, etc.
- Enables code obfuscation and minify for the release build.
- Centralizes all build logic by defining Gradle plugins that other modules can use.
- Changes in this module affect the build configuration of all the other modules, which can be handy.

### Offline-first
When fetching data from the server, Firebase caches the fetched documents and collections. The app primarily loads data from the cache, and only fetches from the server if there is a network connection. This way the cache is the **single source of truth**, as recommended in Android docs.

Firebase takes care of syncing the local cache with the remote server automatically.

## Notes
The app uses the free version of Firebase Firestore, which doesn't include Cloud Functions (CF). CF is Firebase's way of defining server-side logic. Without it, a lot of redundant server calls have to be made client-side (from this case from our Android app).
