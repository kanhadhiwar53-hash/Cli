# MovieBoxProvider.zip integration review

The uploaded `MovieBoxProvider.kt` is a Cloudstream `MainAPI` extension, not a standalone Android app repository.

## What it depends on

- Cloudstream extension classes such as `MainAPI`, `SearchResponse`, `LoadResponse`, and `ExtractorLink`
- Cloudstream's `app.get/app.post` networking helpers
- Jackson Kotlin module and OkHttp request-body helpers
- Cloudstream-specific movie/episode/subtitle models

These classes do not exist in this app's current Kotlin + XML project, so copying the file into `app/src/main/java` will not compile without porting the code and replacing its framework-specific types.

## Integration status

The app currently has a search UI, normalized `MovieItem` model, and `MovieMetadataRepository` interface. The provider source has been reviewed but has **not** been copied into the app.

The source contains private API request-signing logic and methods that resolve playback stream URLs and subtitle URLs. Those parts should not be blindly transplanted into a separate app. Only integrate endpoints and playback sources you are authorized to use, with documented authentication. Do not ship embedded private signing keys, cookies, or third-party app identifiers in the APK.

## Safe next step

Implement a repository adapter against an official/documented metadata API (for example TMDB), map the provider response to `MovieItem`, then render real search results in the existing XML screen. For playback, use authorized streaming-provider links or media URLs you control.
