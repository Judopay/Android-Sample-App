# Judo Android SDK change log

## [5.5.2](https://github.com/judopay/Judo-Android/tree/5.5.2) (2016-11-31)

**Changes:**
- Allow currency to be specified when registering a card, as a default currency of GBP will be used and this can fail if the token payment/pre-auth is made in a different currency.

## [5.5.1](https://github.com/judopay/Judo-Android/tree/5.5.1) (2016-10-27)

**Changes:**
- Use Android API 25 (Android 7.1) SDKs
- Update JudoShield version and bugfixes

## [5.5](https://github.com/judopay/Judo-Android/tree/5.5) (2016-06-29)

**Changes:**
- The SDK is now initialized through a ```Judo``` instance instead of static methods.
- Android SDK is now compiled using Android N (API version 24).
- Image assets have been replaced with vector drawables to reduce SDK size.
- AppCompat and Android Design libraries have been updated to use the latest versions.

**Bugs fixed:**
- Resolved an issue where providing string values for customizable Judo theme attributes resulted in the wrong text being displayed.

## [5.4.1](https://github.com/judopay/Judo-Android/tree/5.4.1) (2016-06-06)

**Changes:**
- Address can be provided in ```JudoOptions``` for use with JudoShield fraud detection.

## [5.4](https://github.com/judopay/Judo-Android/tree/5.4) (2016-05-31)

**Features:**
- Android Pay is now supported by Judo - use Judo's SDK to process payments made with Android Pay, simplifying the checkout experience for your users. See our [guide](https://github.com/JudoPay/Judo-AndroidPay-Sample) on getting started with Android Pay.
- Card scanning can now be launched directly from the card entry form - when a card scanning intent is provided a camera icon will appear next to the card number input field, allowing users to easily input their card number using the device's camera.

**Bugs fixed:**
- Register card now shows the correct 'Add card' text on the submit button when registering a card.

## [5.3](https://github.com/judopay/Judo-Android/tree/5.3) (2016-05-11)

**Features:**
- [Custom layouts](https://github.com/JudoPay/Judo-Android/wiki/Custom-layouts) - you can now provide your own customized layout file when displaying the card input form to the user. This allows you greater freedom in how individual views are presented. See the [guide](https://github.com/JudoPay/Judo-Android/wiki/Custom-layouts) for more information.

**Changes:**
- Activities and fragments in the SDK have been simplified to (```PaymentActivity```, ```PaymentFragment```, ```PreAuthActivity```, ```PreAuthFragment```, ```RegisterCardActivity``` and ```RegisterCardFragment```). When performing a token payment or pre-authorization, just provide a card token in the ```JudoOptions``` configuration and the token will be used for the transaction.
- Button label text, activity title text and the option to turn the security message on/off are now [configured in the theme](https://github.com/JudoPay/Judo-Android/wiki/Themes) instead of the ```JudoOptions``` configuration object.
- When performing a token payment, the security code helper text has changed to "Please re-enter the card security code".
- When performing a payment, the currency provided must be a currency listed in the ```Currency``` class.
- A [ProGuard rules](http://developer.android.com/tools/help/proguard.html) file is now provided in the SDK and will be used if enabled in your gradle configuration, helping you minimize the size of your app.
- Error messages throughout the SDK have been changed to be more useful.

**Bugs fixed:**
- Alignment issues with billing country dropdown have been resolved.
- Settings preferences in the sample app now persist across app restarts.
- Billing postcode has been changed to only accept a max input depending on the billing country selected.

## [5.2](https://github.com/judopay/Judo-Android/tree/5.2) (2016-04-06)
 
**Implemented enhancements:**
 - Card helper images now animate when a change in card type is detected.
 - Better support for using Android Fragments for all transaction types. When using Fragments a callback will be received to indicate the status of the transaction and allowing for more customization. Callbacks using the provided Activity classes remain the same as before.

**Changes:**
 - American Express cards are now accepted by default.
 - Style updates to payment card entry form to match Google Material Design guidelines.
 - Judo API token and secret can now be specified directly in the Android Manifest instead of programmatically.
 - Client integration method reporting data is now sent with the transaction to indicate if a custom UI or judo provided UI is used.
 - Joda-Time library has been removed from the SDK to avoid issues with the app method count reaching the [65k dex method limit](http://developer.android.com/tools/building/multidex.html).
 - The HTTP client library Retrofit has been upgraded to version 2.0.0.
 - The security code hint text for American Express cards has changed from CIDV to CID.
 - Activity Intent constants have been removed in favour of using the ```JudoOptions``` builder to configure customization options when calling the SDK.

**Bugs fixed:**
- Start date and issue number fields will no longer be shown when making a token payment.
- When using a device in landscape orientation the billing postcode will no longer go into fullscreen input mode when the field has focus.
- When entering an Amex card number longer than 15 digits, the SDK will no longer crash.
- The card number, expiry date and start date had some formatting issues if characters were input into the middle of the text after initial entry.
- Back button now works correctly when rotation has been changed whilst on the payment screen for JellyBean devices.

## [5.1.2](https://github.com/judopay/Judo-Android/tree/5.1.2) (2016-02-29)

**Bugs fixed:**
 - ```VoidRequest```, ```CollectionRequest``` and ```RefundRequest``` no longer accept a 'yourPaymentReference' constructor argument as the request can be detected as a duplicate if the same payment reference was used from the pre-auth or payment request.

**Changes:**
- When performing a token payment, the expiry date of the token card is now shown along with the card number last 4 digits.
- Request classes renamed: PaymentRequest, TokenRequest, RegisterCardRequest, VoidRequest, RefundRequest, CollectionRequest.
- CV2 card location icon now shown on launch of payment screen, previously was only shown after card number input.

## [5.1.1](https://github.com/judopay/Judo-Android/tree/5.1.1) (2016-02-11)

**Implemented enhancements:**
- Added support for voiding pre-auth transactions to ```JudoApiService```

**Bugs fixed:**
- Visa Credit, Visa Debit and Visa Electron now show Visa logo when performing a token payment or pre-auth
- Fixed an issue where additional API requests would fail after an initial transaction request was performed.
- Resolved an issue where making a token payment/pre-auth would not allow a 4 digit CIDV to be entered for an Amex card.
- Resolved an issue where an incorrect error message was given if the required Activity Extras was not passed to a ```JudoActivity```.
- Fixed an issue where the transaction metadata was not sent for register card transactions.
- Fixed an issue where the incorrect dialog message was shown when a card was declined during a card registration.

**Changes:**
- When a duplicated transaction is detected, a ```DuplicateTransactionException``` is now thrown instead of the original transaction response being replayed.
- UI tests moved to judo-sdk module and test framework upgraded to Espresso 2.
- Retrofit library upgraded to latest version.
- New method for obtaining ```JudoApiService``` instance, call ```Judo.getApiService(context);```
- ```JudoApiService``` method for completing a transaction with 3D Secure verification renamed.
- Address Verification Service (AVS) postcode field can now be skipped if card holder is outside UK, USA and Canada.  
- Removed the ability to pass pre-filled card data into the card entry form when performing a token payment/pre-auth

## [5.1](https://github.com/judopay/Judo-Android/tree/5.1) (2016-01-28)

**Implemented enhancements:**
- Brazilian Real added to list of supported currencies.
- Enable 3D Secure verification for all transactions by default, if required by the Merchant's bank.
- Allow transactions for Android devices with root permissions, with option to block if required.
- New Android views provided for card data entry for use when writing a custom UI.
- Display secure server transmission text in the payment form, to indicate transaction is performed securely.

**Bugs fixed:**
- Fixed a crashing issue that could occur if an Activity was restarted due to low memory.
- Resolved an issue where the postcode field hint would not render correctly when switching between countries.
- Fixed an issue where the postcode field would not pick up the theme's tint color on pre-Lollipop devices.
- Visa Electron/Visa Debit now correctly detected as a Visa card during card number entry.

**Changes:**
- New ```JudoOptions``` class for sending type safe data between Activity and Fragment instances.
- New fields included in Transaction classes for setting mobile number and email address.

## [5.0.1](https://github.com/judopay/Judo-Android/tree/5.0.1) (2016-01-12)

**Implemented enhancements:**
- SHA 256 SSL/TLS Certificate upgrade - an industry-wide security update to protect you against man-in-the-middle attacks
- Maestro card type support is now enabled in the SDK by default.
- The sample app settings page now includes the full list of current and future supported currencies.
- A more useful error message is provided if the judoNative SDK is not properly initialized.
- Duplication prevention built in to protect merchants and consumers against duplicated transactions via unique payment reference.

**Bugs fixed:**
- Fixed an issue that prevented card details from being amended when attempting to register a card with validation errors.
- Resolved a crashing issue that would occur when any API error was encountered when performing a transaction, due to ApiError class not being Parcelable when attempting to pass back the Receipt for the transaction.
- Fixed an issue with card digits being skipped when attempting to type into the card number input field.
- Merged judo-sdk and judo-data modules due to an issue with resolving library internal dependencies found when releasing previous SDK version.

**Changes:**
- Renamed JudoPay initialization class to be called Judo, for consistency across SDK platforms.

## [5.0](https://github.com/judopay/Judo-Android/tree/5.0) (2015-12-10)

**Implemented enhancements:**
- Complete redesign of all screens to use Material Design guidelines - this is a set of guidelines set by Google and the way that most new apps are being designed.
- New design for tablet devices, with payment button in-line.
- The UI of the payment form can now be customized more easily, using Android themes it's possible to override the colours used (e.g. the action bar, background colour, etc.).
- 3D Secure feature implemented and shown when required by the merchant. This will show a web view overlaying the payment form.
- judoShield integration for automatic fraud detection.
- Detection of rooted Android devices, with option to block payments from insecure devices.
- Important change for PCI compliance - SSL settings have been changed to make our SDK compliant and more secure. Due to this change we have had to drop support for older Android versions (prior to Jelly Bean OS, released on phones in 2012).
- SDK is now easier to integrate than ever, due to upgrading the build tools used by the project. Only takes 3 lines of code to bring up a payment screen.
- SDK can now automatically be downloaded from jCenter repository, for one line install of library into app project.
- [Javadocs published to GitHub](http://judopay.github.io/Judo-Android/)

**Bugs fixed:**
- Resolved an issue where token payment/pre-auth could not be performed due to the judo ID was not required when registering cards, this caused a problem when having multiple judo IDs in your account with multiple locations.

**Changed:**
- New class JudoPay, replacing JudoSDKManager for performing initial setup and turning on/off configuration options.
- New class JudoApiService for interacting with JudoPay REST endpoints, using Retrofit 2.0 and rxjava Observable (replaces TransactionQueryApiService, TransactionProcessingApiService, PaymentAction, RegisterAction and TransactionAction).
- Domain classes used with JudoPay REST API moved to judo-data module, new package: com.judopay.model .

**Build changes:**
- SDK now built in Android archive format (.aar).
- SDK file size reduced by 80% (now 215 KB).
- Publish tasks for uploading to bintray.
