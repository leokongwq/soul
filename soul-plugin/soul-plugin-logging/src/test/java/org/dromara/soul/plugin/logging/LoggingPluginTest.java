/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.plugin.logging;

import java.net.InetSocketAddress;
import org.dromara.soul.common.dto.RuleData;
import org.dromara.soul.common.dto.SelectorData;
import org.dromara.soul.common.enums.PluginEnum;
import org.dromara.soul.plugin.api.SoulPluginChain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * The Test Case For DebugPlugin.
 *
 * @author xuxd
 **/
@RunWith(MockitoJUnitRunner.class)
public final class LoggingPluginTest {

    private LoggingPlugin loggingPlugin;

    private ServerWebExchange exchange;

    private RuleData ruleData;

    private SoulPluginChain chain;

    private SelectorData selectorData;

    @Before
    public void setUp() {
        this.loggingPlugin = new LoggingPlugin();
        this.ruleData = mock(RuleData.class);
        this.chain = mock(SoulPluginChain.class);
        this.selectorData = mock(SelectorData.class);
        MockServerHttpRequest request = MockServerHttpRequest
            .post("/path")
            .body("request body")
            .get("localhost")
            .remoteAddress(new InetSocketAddress(8090))
            .header("X-source", "mock test")
            .queryParam("queryParam", "Hello,World")
            .build();
        this.exchange = spy(MockServerWebExchange.from(request));
    }

    @Test
    public void testDoExecute() {
        ServerWebExchange.Builder builder = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(builder);
        when(builder.request(any(LoggingPlugin.LoggingServerHttpRequest.class))).thenReturn(builder);
        when(builder.response(any(LoggingPlugin.LoggingServerHttpResponse.class))).thenReturn(builder);
        when(builder.build()).thenReturn(exchange);
        when(chain.execute(any())).thenReturn(Mono.empty());
        Mono<Void> result = loggingPlugin.doExecute(exchange, chain, selectorData, ruleData);
        // Sorry, I do not how to mock this case by an simply way, so I give up.

        StepVerifier.create(result).expectSubscription().verifyComplete();
    }

    @Test
    public void testGetOrder() {
        Assert.assertEquals(loggingPlugin.getOrder(), PluginEnum.Logging.getCode());
    }

    @Test
    public void testNamed() {
        Assert.assertEquals(loggingPlugin.named(), PluginEnum.Logging.getName());
    }

    @Test
    public void testSkip() {
        Assert.assertFalse(loggingPlugin.skip(exchange));
    }
}
