/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.accounts.chooser;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;


/**
 * This class provides access to a centralized registry of the user's
 * online accounts.  The user enters credentials (username and password) once
 * per account, granting applications access to online resources with
 * "one-click" approval.
 * <p/>
 * <p>Different online services have different ways of handling accounts and
 * authentication, so the account manager uses pluggable <em>authenticator</em>
 * modules for different <em>account types</em>.  Authenticators (which may be
 * written by third parties) handle the actual details of validating account
 * credentials and storing account information.  For example, Google, Facebook,
 * and Microsoft Exchange each have their own authenticator.
 * <p/>
 * <p>Many servers support some notion of an <em>authentication token</em>,
 * which can be used to authenticate a request to the server without sending
 * the user's actual password.  (Auth tokens are normally created with a
 * separate request which does include the user's credentials.)  AccountManager
 * can generate auth tokens for applications, so the application doesn't need to
 * handle passwords directly.  Auth tokens are normally reusable and cached by
 * AccountManager, but must be refreshed periodically.  It's the responsibility
 * of applications to <em>invalidate</em> auth tokens when they stop working so
 * the AccountManager knows it needs to regenerate them.
 * <p/>
 * <p>Applications accessing a server normally go through these steps:
 * <p/>
 * <ul>
 * <li>Get an instance of AccountManager using {@link AccountManager#get(Context)}.
 * <p/>
 * <li>List the available accounts using {@link AccountManager#getAccountsByType} or
 * {@link AccountManager#getAccountsByTypeAndFeatures}.  Normally applications will only
 * be interested in accounts with one particular <em>type</em>, which
 * identifies the authenticator.  Account <em>features</em> are used to
 * identify particular account subtypes and capabilities.  Both the account
 * type and features are authenticator-specific strings, and must be known by
 * the application in coordination with its preferred authenticators.
 * <p/>
 * <li>Select one or more of the available accounts, possibly by asking the
 * user for their preference.  If no suitable accounts are available,
 * {@link AccountManager#addAccount} may be called to prompt the user to create an
 * account of the appropriate type.
 * <p/>
 * <li><b>Important:</b> If the application is using a previously remembered
 * account selection, it must make sure the account is still in the list
 * of accounts returned by {@link AccountManager#getAccountsByType}.  Requesting an auth token
 * for an account no longer on the device results in an undefined failure.
 * <p/>
 * <li>Request an auth token for the selected account(s) using one of the
 * {@link AccountManager#getAuthToken} methods or related helpers.  Refer to the description
 * of each method for exact usage and error handling details.
 * <p/>
 * <li>Make the request using the auth token.  The form of the auth token,
 * the format of the request, and the protocol used are all specific to the
 * service you are accessing.  The application may use whatever network and
 * protocol libraries are useful.
 * <p/>
 * <li><b>Important:</b> If the request fails with an authentication error,
 * it could be that a cached auth token is stale and no longer honored by
 * the server.  The application must call {@link AccountManager#invalidateAuthToken} to remove
 * the token from the cache, otherwise requests will continue failing!  After
 * invalidating the auth token, immediately go back to the "Request an auth
 * token" step above.  If the process fails the second time, then it can be
 * treated as a "genuine" authentication failure and the user notified or other
 * appropriate actions taken.
 * </ul>
 * <p/>
 * <p>Some AccountManager methods may need to interact with the user to
 * prompt for credentials, present options, or ask the user to add an account.
 * The caller may choose whether to allow AccountManager to directly launch the
 * necessary user interface and wait for the user, or to return an Intent which
 * the caller may use to launch the interface, or (in some cases) to install a
 * notification which the user can select at any time to launch the interface.
 * To have AccountManager launch the interface directly, the caller must supply
 * the current foreground {@link Activity} context.
 * <p/>
 * <p>Many AccountManager methods take {@link AccountManagerCallback} and
 * {@link Handler} as parameters.  These methods return immediately and
 * run asynchronously. If a callback is provided then
 * {@link AccountManagerCallback#run} will be invoked on the Handler's
 * thread when the request completes, successfully or not.
 * The result is retrieved by calling {@link AccountManagerFuture#getResult()}
 * on the {@link AccountManagerFuture} returned by the method (and also passed
 * to the callback).  This method waits for the operation to complete (if
 * necessary) and either returns the result or throws an exception if an error
 * occurred during the operation.  To make the request synchronously, call
 * {@link AccountManagerFuture#getResult()} immediately on receiving the
 * future from the method; no callback need be supplied.
 * <p/>
 * <p>Requests which may block, including
 * {@link AccountManagerFuture#getResult()}, must never be called on
 * the application's main event thread.  These operations throw
 * {@link IllegalStateException} if they are used on the main thread.
 */
public class AccountChooser {
    @SuppressWarnings("unused")
    private static final String TAG = "SupportAccountManager";

    /**
     * Returns an intent to an {@link Activity} that prompts the user to choose from a list of
     * accounts.
     * The caller will then typically start the activity by calling
     * <code>startActivityWithResult(intent, ...);</code>.
     * <p/>
     * On success the activity returns a Bundle with the account name and type specified using
     * keys {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE}.
     * <p/>
     * The most common case is to call this with one account type, e.g.:
     * <p/>
     * <pre>  newChooseAccountsIntent(null, null, new String[]{"com.google"}, false, null,
     * null, null, null);</pre>
     *
     * @param selectedAccount            if specified, indicates that the {@link android.accounts.Account} is the
     *                                   currently
     *                                   selected one, according to the caller's definition of selected.
     * @param allowableAccounts          an optional {@link java.util.ArrayList} of accounts that are allowed to be
     *                                   shown. If not specified then this field will not limit the displayed accounts.
     * @param allowableAccountTypes      an optional string array of account types. These are used
     *                                   both to filter the shown accounts and to filter the list of account types that
     *                                   are shown
     *                                   when adding an account.
     * @param alwaysPromptForAccount     if set the account chooser screen is always shown, otherwise
     *                                   it is only shown when there is more than one account from which to choose
     * @param descriptionOverrideText    if non-null this string is used as the description in the
     *                                   accounts chooser screen rather than the default
     * @param addAccountAuthTokenType    this string is passed as the {@link AccountManager#addAccount}
     *                                   authTokenType parameter
     * @param addAccountRequiredFeatures this string array is passed as the {@link AccountManager#addAccount}
     *                                   requiredFeatures parameter
     * @param addAccountOptions          This {@link android.os.Bundle} is passed as the {@link
     *                                   AccountManager#addAccount}
     *                                   options parameter
     * @param context                    The Context of the calling app
     *
     * @return an {@link Intent} that can be used to launch the ChooseAccount activity flow.
     */
    public static Intent newChooseAccountIntent(Account selectedAccount,
                                                ArrayList<Account> allowableAccounts,
                                                String[] allowableAccountTypes,
                                                boolean alwaysPromptForAccount,
                                                String descriptionOverrideText,
                                                String addAccountAuthTokenType,
                                                String[] addAccountRequiredFeatures,
                                                Bundle addAccountOptions,
                                                Context context) {

        // TODO: re-enable after testing
        /*
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
    		return AccountManager.newChooseAccountIntent(
    				selectedAccount, allowableAccounts, allowableAccountTypes,
    				alwaysPromptForAccount, descriptionOverrideText,
    				addAccountAuthTokenType, addAccountRequiredFeatures, addAccountOptions);
    	}
    	*/

        Intent intent = new Intent();
        intent.setClassName(context,
                            ChooseTypeAndAccountActivity.class.getName());

        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST,
                        allowableAccounts);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY,
                        allowableAccountTypes);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE,
                        addAccountOptions);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_SELECTED_ACCOUNT, selectedAccount);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ALWAYS_PROMPT_FOR_ACCOUNT,
                        alwaysPromptForAccount);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_DESCRIPTION_TEXT_OVERRIDE,
                        descriptionOverrideText);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_AUTH_TOKEN_TYPE_STRING,
                        addAccountAuthTokenType);
        intent.putExtra(
            ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY,
            addAccountRequiredFeatures);
        return intent;
    }
}
