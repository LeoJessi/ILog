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
 * 内容黑名单
 *
 * @since 1.236.8
 */
public class BlacklistMsgFilterInterceptor extends AbstractFilterInterceptor {

    private final Iterable<String> blacklistMsg;

    /**
     * Constructor
     *
     * @param blacklistMsg the blacklist msg, the logs with a msg that is in the blacklist will be
     *                     filtered out
     */
    public BlacklistMsgFilterInterceptor(String... blacklistMsg) {
        this(Arrays.asList(blacklistMsg));
    }

    /**
     * Constructor
     *
     * @param blacklistMsg the blacklist msg, the logs with a msg that is in the blacklist will be
     *                     filtered out
     */
    public BlacklistMsgFilterInterceptor(Iterable<String> blacklistMsg) {
        if (blacklistMsg == null) {
            throw new NullPointerException();
        }
        this.blacklistMsg = blacklistMsg;
    }

    /**
     * {@inheritDoc}
     *
     * @return true if the msg of the log is in the blacklist, false otherwise
     */
    @Override
    protected boolean reject(LogItem log) {
        if (blacklistMsg != null) {
            for (String disabledMsg : blacklistMsg) {
                if (log.msg.contains(disabledMsg)) return true;
            }
        }
        return false;
    }
}
