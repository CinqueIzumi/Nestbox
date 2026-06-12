# Nestbox

Nestbox is an **unofficial companion app** for reading the Doveletter. Subscribers can connect
their GitHub account to browse the Doveletter's articles and content, and keep track of what
they've read.

> Nestbox is not affiliated with, endorsed by, or officially connected to the Doveletter or
> GitHub. It is a community-built reader.

---

## ⚠️ Important: GitHub access scope

> **Connecting your GitHub account technically grants Nestbox read access to _every_ repository
> your account can reach — not just the Doveletter repository.**
>
> This is an unavoidable limitation of GitHub's OAuth device flow: its `repo` scope is
> all-or-nothing and **cannot** be restricted to a single repository. Worse, the `repo` scope is
> **read _and_ write** — the token GitHub issues is technically capable of modifying any repository
> your account can reach. Nestbox only ever _reads_ the Doveletter repository and never writes or
> touches anything else, but the token it holds is broad and write-capable by GitHub's design.
>
> **Treat the stored token and your connected account with care:**
> - The token is stored encrypted on-device (AndroidKeyStore-backed AES/GCM) and is never sent
>   anywhere except GitHub.
> - Disconnect from the Profile screen at any time to delete the token locally.
> - To fully revoke access, remove the authorization at
>   **GitHub → Settings → Applications → Authorized OAuth Apps**.
>
> If broad access is a concern, consider using a dedicated GitHub account that only has access to
> the Doveletter repository.

---

## Setup

Nestbox authenticates with GitHub using the OAuth **device flow**, so no client secret is shipped
in the app.

1. Create a GitHub **OAuth App**: GitHub → Settings → Developer settings → OAuth Apps → New OAuth
   App.
   - **Application name:** `Nestbox` (this is the most prominent text on the consent screen)
   - **Homepage URL:** your project/repo URL
   - **Authorization callback URL:** required by the form but unused by the device flow — any valid
     URL works (e.g. the homepage URL)
   - **Enable Device Flow:** ✅ (required)
2. Copy the generated **Client ID**.
3. Add it to `local.properties` (kept out of version control):
   ```properties
   GITHUB_CLIENT_ID=Iv1.xxxxxxxxxxxx
   ```
   It is exposed to the app via `BuildConfig.GITHUB_CLIENT_ID`. The client id is not a secret, but
   lives in `local.properties` so it isn't committed.

### Linking an account

Open the **Profile** tab → **Connect GitHub**. Nestbox shows a short code; tap **Copy code & open
GitHub**, paste the code on GitHub, and approve. The app polls until approval completes, then
stores the token encrypted on-device.

---

## Architecture

Nestbox uses Jetpack Compose, Koin (DI), and Voyager (navigation), with a home-grown **TOAD**
presentation architecture. Before adding a feature, read
[`docs/toad-architecture.md`](docs/toad-architecture.md).

## Build

```sh
./gradlew assembleDebug
```

- min SDK 24, target/compile SDK 37
