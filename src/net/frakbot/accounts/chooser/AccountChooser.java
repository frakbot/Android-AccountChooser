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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * This class can be used in order to get an Intent for an Account selection.
 * The method will then check for the current build version and call the native
 * implementation or a backported code from API Level 17.
 * 
 * @see {@link AccountChooser#newChooseAccountIntent(Account, ArrayList, String[], boolean, String, String, String[], Bundle, Context)}
 * @author The Android Open Source Project
 * @author Francesco Pontillo
 */
public class AccountChooser {
	@SuppressWarnings("unused")
	private static final String TAG = "SupportAccountManager";

	/**
	 * Returns an intent to an {@link Activity} that prompts the user to choose
	 * from a list of accounts. The caller will then typically start the
	 * activity by calling <code>startActivityWithResult(intent, ...);</code>.
	 * <p/>
	 * On success the activity returns a Bundle with the account name and type
	 * specified using keys {@link AccountManager#KEY_ACCOUNT_NAME} and
	 * {@link AccountManager#KEY_ACCOUNT_TYPE}.
	 * <p/>
	 * The most common case is to call this with one account type, e.g.:
	 * <p/>
	 * 
	 * <pre>
	 * newChooseAccountsIntent(null, null, new String[] { &quot;com.google&quot; }, false, null,
	 * 		null, null, null);
	 * </pre>
	 * 
	 * @param selectedAccount
	 *            if specified, indicates that the
	 *            {@link android.accounts.Account} is the currently selected
	 *            one, according to the caller's definition of selected.
	 * @param allowableAccounts
	 *            an optional {@link java.util.ArrayList} of accounts that are
	 *            allowed to be shown. If not specified then this field will not
	 *            limit the displayed accounts.
	 * @param allowableAccountTypes
	 *            an optional string array of account types. These are used both
	 *            to filter the shown accounts and to filter the list of account
	 *            types that are shown when adding an account.
	 * @param alwaysPromptForAccount
	 *            if set the account chooser screen is always shown, otherwise
	 *            it is only shown when there is more than one account from
	 *            which to choose
	 * @param descriptionOverrideText
	 *            if non-null this string is used as the description in the
	 *            accounts chooser screen rather than the default
	 * @param addAccountAuthTokenType
	 *            this string is passed as the {@link AccountManager#addAccount}
	 *            authTokenType parameter
	 * @param addAccountRequiredFeatures
	 *            this string array is passed as the
	 *            {@link AccountManager#addAccount} requiredFeatures parameter
	 * @param addAccountOptions
	 *            This {@link android.os.Bundle} is passed as the
	 *            {@link AccountManager#addAccount} options parameter
	 * @param context
	 *            The Context of the calling app
	 * 
	 * @return an {@link Intent} that can be used to launch the ChooseAccount
	 *         activity flow.
	 */
	@SuppressLint("NewApi")
	public static Intent newChooseAccountIntent(Account selectedAccount,
			ArrayList<Account> allowableAccounts,
			String[] allowableAccountTypes, boolean alwaysPromptForAccount,
			String descriptionOverrideText, String addAccountAuthTokenType,
			String[] addAccountRequiredFeatures, Bundle addAccountOptions,
			Context context) {

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return AccountManager.newChooseAccountIntent(selectedAccount,
					allowableAccounts, allowableAccountTypes,
					alwaysPromptForAccount, descriptionOverrideText,
					addAccountAuthTokenType, addAccountRequiredFeatures,
					addAccountOptions);
		}

		Intent intent = new Intent();
		intent.setClassName(context,
				ChooseTypeAndAccountActivity.class.getName());

		intent.putExtra(
				ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST,
				allowableAccounts);
		intent.putExtra(
				ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY,
				allowableAccountTypes);
		intent.putExtra(
				ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE,
				addAccountOptions);
		intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_SELECTED_ACCOUNT,
				selectedAccount);
		intent.putExtra(
				ChooseTypeAndAccountActivity.EXTRA_ALWAYS_PROMPT_FOR_ACCOUNT,
				alwaysPromptForAccount);
		intent.putExtra(
				ChooseTypeAndAccountActivity.EXTRA_DESCRIPTION_TEXT_OVERRIDE,
				descriptionOverrideText);
		intent.putExtra(
				ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_AUTH_TOKEN_TYPE_STRING,
				addAccountAuthTokenType);
		intent.putExtra(
				ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY,
				addAccountRequiredFeatures);
		return intent;
	}
}
