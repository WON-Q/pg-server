package com.fisa.pg.feign.client;

import com.fisa.pg.config.feign.WonQClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "wonqClient", url = "${app.wonq.endpoint}", configuration = WonQClientConfig.class)
public interface WonQClient {

}
