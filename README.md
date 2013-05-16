# Android-AccountChooser

---

**Account Chooser** for Android, backported from JellyBean.

`Android-AccountChooser` is a library that enables you to use the same method as `AccountManager#newChooseAccountIntent` (see the [official documentation](http://developer.android.com/reference/android/accounts/AccountManager.html)) introduced in API 14, from API Level 8 forward.

## Authors

**Francesco Pontillo** and **Sebastiano Poggi**.

## How to use

First, you have to include the given project as a library reference in your application project.

Then, include the following Activities in your `AndroidManifest.xml`:

```xml
	<activity android:name="net.frakbot.accounts.chooser.ChooseTypeAndAccountActivity" />
	<activity android:name="net.frakbot.accounts.chooser.ChooseAccountActivity" />
	<activity android:name="net.frakbot.accounts.chooser.ChooseAccountTypeActivity" />
```

In the end call, instead of `AccountManager#newChooseAccountIntent`:

```java
AccountChooser.newChooseAccountIntent(
	selectedAccount, allowableAccounts, allowableAccountTypes,
	alwaysPromptForAccount, descriptionOverrideText, addAccountAuthTokenType,
	addAccountRequiredFeatures, addAccountOptions);
```

`AccountChooser` will then automatically detect the `Build.VERSION` number and use the most appropriate implementation, choosing from:

- the standard device implementation, if `Build.VERSION >= 14`
- the backported implementation from the API Level 17

See the [official documentation](http://developer.android.com/reference/android/accounts/AccountManager.html) for more help, as the behaviour is just the same.

## License

```
Copyright 2013 Francesco Pontillo and Sebastiano Poggi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
