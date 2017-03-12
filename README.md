# Judopay Android SDK [ ![Download](https://api.bintray.com/packages/judopay/maven/android-sdk/images/download.svg) ](https://bintray.com/judopay/maven/android-sdk/_latestVersion)

The Judopay Android library lets you integrate secure in-app card payments into your Android app.

Use our UI components for a seamless user experience for card data capture. Minimise your [PCI scope](https://www.pcisecuritystandards.org/pci_security/completing_self_assessment) with a UI that can be themed or customised to match the look and feel of your app.

##### Android Pay has launched in the UK. You can use this SDK to process Android Pay. For more information, please have a look at our [sample app](https://github.com/JudoPay/Judo-AndroidPay-Sample).

##### **\*\*\*Due to industry-wide security updates, versions below 5.0 of this SDK will no longer be supported after 1st Oct 2016. For more information regarding these updates, please read our blog [here](http://hub.judopay.com/pci31-security-updates/).*****

## Requirements
Please ensure the following requirements are met when integrating the SDK:
- Android Studio 2.3+ is installed
- Android SDK build tools 25.0.2 is installed
- The minimum SDK level of your app is Android Jelly Bean (API level 16) or higher
- Android Support Repository and Android Support Library have the latest version installed.

## Getting started
##### 1. Integration
Installation of the Judopay Android SDK is simple using Android Studio, you don't need to clone a repository, just add the dependency to your ```app/build.gradle``` file:
```groovy
compile 'com.judopay:android-sdk:5.6.5'
```

In your project-level build.gradle file you will need to add the Judopay Maven repository:
```groovy
allprojects {
    repositories {
        jcenter()
        maven {
            url "http://dl.bintray.com/judopay/maven"
        }
    }
}
```

##### 2. Setup
Initialize the Judopay SDK with your [Judo ID, token and secret](https://portal.judopay.com/account/settings):
```java
Judo judo = new Judo.Builder()
    .setApiToken("<TOKEN>")
    .setApiSecret("<SECRET>")
    .setEnvironment(Judo.SANDBOX)
    .setJudoId("<JUDO_ID>")
    .setAmount("1.00")
    .setCurrency(Currency.GBP)
    .setConsumerReference("<YOUR_REFERENCE>")
    .build();
```
**Note:** Please ensure that you are using a unique consumerReference for each request.

##### 3. Make a payment
To show the payment screen, create an Intent for the `PaymentActivity` with the ```Judo``` instance:
```java
Intent intent = new Intent(activity, PaymentActivity.class);
intent.putExtra(Judo.JUDO_OPTIONS, judo);

startActivityForResult(intent, PAYMENT_REQUEST);
```
##### 4. Check the payment result
In the Activity that calls the Judopay SDK, override the ```onActivityResult``` method to receive the payment receipt:
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == PAYMENT_REQUEST) {
        switch (resultCode) {
            case Judo.RESULT_SUCCESS:
                Receipt receipt = data.getParcelableExtra(Judo.JUDO_RECEIPT);
                // handle successful payment
        }
    }
}
```

## Next steps
The Judopay Android SDK supports a range of customization options. For more information see our [wiki documentation](https://github.com/Judopay/Android-Sample-App/wiki). 

## License
See the [LICENSE](https://github.com/Judopay/Android-Sample-App/blob/master/LICENSE) file for license rights and limitations (MIT).
