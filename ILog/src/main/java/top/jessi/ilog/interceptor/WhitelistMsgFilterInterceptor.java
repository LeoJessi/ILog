/*
 * Copyright 2016 Elvis Hew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.jessi.ilog.interceptor;

import java.util.Arrays;

import top.jessi.ilog.LogItem;

/**
 * 内容白名单
 *
 * @since 1.236.8
 */
public class WhitelistMsgFilterInterceptor extends AbstractFilterInterceptor {

    private final Iterable<String> whitelistMsg;

    /**
     * Constructor
     *
     * @param whitelistMsg the whitelist msg, the logs with a msg that is NOT in the whitelist
     *                     will be filtered out
     */
    public WhitelistMsgFilterInterceptor(String... whitelistMsg) {
        this(Arrays.asList(whitelistMsg));
    }

    /**
     * Constructor
     *
     * @param whitelistMsg the whitelist msg, the logs with a msg that is NOT in the whitelist
     *                     will be filtered out
     */
    public WhitelistMsgFilterInterceptor(Iterable<String> whitelistMsg) {
        if (whitelistMsg == null) {
            throw new NullPointerException();
        }
        this.whitelistMsg = whitelistMsg;
    }

    /**
     * {@inheritDoc}
     *
     * @return true if the msg of the log is NOT in the whitelist, false otherwise
     */
    @Override
    protected boolean reject(LogItem log) {
        if (whitelistMsg != null) {
            for (String enabledMsg : whitelistMsg) {
                if (log.msg.contains(enabledMsg)) return false;
            }
        }
        return true;
    }
}
