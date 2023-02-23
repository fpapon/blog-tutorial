/*
 * Copyright (c) 2019-Present - François Papon - Openobject.fr - https://openobject.fr
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fr.openobject.blog.tutorial.fusion;

import io.yupiik.fusion.framework.api.ConfiguringContainer;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static java.lang.System.exit;

public class FusionMain {

    private static final Logger logger = Logger.getLogger(FusionMain.class.getName());

    /*
    jdbc:h2:file:./target/test;INIT=RUNSCRIPT FROM './src/test/resources/create.sql'
     */

    public static void main(String[] args) {
        try (final var container = ConfiguringContainer.of().start()) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            logger.info("started!");
            countDownLatch.await();
        } catch (InterruptedException e) {
            exit(1);
        }
        exit(0);
    }
}
