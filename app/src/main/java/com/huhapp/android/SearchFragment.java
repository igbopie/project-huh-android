/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.huhapp.android;

import com.huhapp.android.api.Api;
import com.huhapp.android.util.QuestionTabsFragment;

public class SearchFragment extends QuestionTabsFragment {

    @Override
    protected QuestionTabsFragment.QuestionListPagerItem[] getPagerItems() {
        QuestionListPagerItem[] items = new QuestionListPagerItem[3];
        items[0] = new QuestionListPagerItem("Latest", Api.ENDPOINT_QUESTIONS_LATEST);
        items[1] = new QuestionListPagerItem("Trending", Api.ENDPOINT_QUESTIONS_TRENDING);
        items[2] = new QuestionListPagerItem("Near", Api.ENDPOINT_QUESTIONS_NEAR);
        return items;
    }
}
