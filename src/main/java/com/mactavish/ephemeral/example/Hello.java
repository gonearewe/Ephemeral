package com.mactavish.ephemeral.example;

import com.mactavish.ephemeral.Bootstrap;

class Hello {
    static void start() throws Exception {
        new Bootstrap().run(Hello.class);
    }
}
