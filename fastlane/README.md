fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android build_release

```sh
[bundle exec] fastlane android build_release
```

Build a release AAB

### android deploy_production

```sh
[bundle exec] fastlane android deploy_production
```

Upload to Google Play Console (Production)

### android deploy_internal

```sh
[bundle exec] fastlane android deploy_internal
```

Upload to Google Play Internal Testing

### android install_debug

```sh
[bundle exec] fastlane android install_debug
```

Build and install debug version on connected device

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
