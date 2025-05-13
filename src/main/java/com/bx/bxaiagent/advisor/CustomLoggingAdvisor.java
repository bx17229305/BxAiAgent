package com.bx.bxaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

public class CustomLoggingAdvisor implements CallAroundAdvisor,StreamAroundAdvisor {


    @Override
    public AdvisedResponse aroundCall(AdvisedRequest request, CallAroundAdvisorChain chain) {
     
        // 2. 调用链中的下一个Advisor或ChatModel
        AdvisedResponse response = chain.nextAroundCall(request);
        
     
        return response;
    }

    /**
     * 返回类名
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(advisedRequest);
    }
}