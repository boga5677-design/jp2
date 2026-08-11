# Nihongo Go signing identity

This Japanese edition intentionally uses a different Android application ID and signing key from PetLingo.

- applicationId: `com.nihongogo.learning`
- keystore: `signing/nihongo-go-update.jks`
- alias: `nihongogo`
- certificate owner: `CN=Nihongo Go Update, O=Nihongo Go, C=TW`

Do not replace this keystore in future Nihongo Go releases. Android updates must be signed by the same key.
The original PetLingo keystore remains in the source archive only for reference and is not used by this app.
