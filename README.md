# MovieBox Integration Starter

Native Kotlin + XML Android app based on the uploaded `MovieBoxProvider.zip` source.

## Implemented

- Native movie/series search screen, loading state, and error handling.
- Kotlin repository adapter to normalize search results into movie/TV models.
- Configurable HTTPS backend URL, without embedding private credentials.
- GitHub Actions workflow to build a debug APK artifact.

## About the uploaded ZIP

The ZIP contains a Cloudstream extension, not a standalone Android SDK. It depends on Cloudstream interfaces. Its source also includes internal request-signing/token logic and stream-resolution routines. This native app does not copy private signing logic or extract protected playback links. Use a backend/provider only where you have authorization.

## MovieBox backend setup

The app expects an HTTPS backend that you control and are authorized to use. Configure the Gradle project property:

```properties
MOVIEBOX_BACKEND_URL=https://your-authorized-backend.example
```

Set this in local Gradle properties or the build environment; do not commit credentials. If the property is empty, the app still builds but shows a setup message.

### Search endpoint

`GET {MOVIEBOX_BACKEND_URL}/search?q=The%20Matrix`

Expected JSON:

```json
{
  "results": [
    {
      "id": "example-id",
      "title": "Example title",
      "type": "movie",
      "overview": "Description",
      "posterUrl": "https://example.org/poster.jpg",
      "releaseDate": "2025-01-01"
    }
  ]
}
```

The adapter also accepts `items` instead of `results`, and `subjectId` instead of `id`.

## Build

Push to `main` or manually run **Build Debug APK** in GitHub Actions. The APK is uploaded as an artifact. Set `MOVIEBOX_BACKEND_URL` in the build environment if the generated APK should connect to your authorized backend.
