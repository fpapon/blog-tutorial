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
package fr.openobject.blog.tutorial.reactive;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.lang.System.exit;

public class ReactiveApp {

    private static final Logger logger = Logger.getLogger(ReactiveApp.class.getName());
    private static final DateTimeFormatter dtFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(TimeZone.getTimeZone("UTC").toZoneId());
    private static final Random random = new Random();

    public static void main(String[] args) {
        logger.info(ReactiveApp.class.getSimpleName().concat(" started"));
        logger.info(" step 1");
        Instant start = new Date().toInstant();
        List<CompletableFuture<Supplier<String>>> completableFutures = Stream.of("one","two","three","four","five")
                .map(ReactiveApp::convert)
                .toList();

        logger.info("step 2");
        CompletableFuture.allOf(
                completableFutures.toArray(
                        new CompletableFuture[completableFutures.size()])).whenComplete((value, th) ->
                                completableFutures.forEach(msg -> logger.info("number :: ".concat(msg.getNow(() -> "null").get()))));
        Instant end = new Date().toInstant();
        logger.info("Start " + dtFormatter.format(start) + " | End " + dtFormatter.format(end) + " | Duration " + Duration.between(start, end).getSeconds());
        logger.info(ReactiveApp.class.getSimpleName().concat(" ended"));
        exit(0);
    }

    private static CompletableFuture<Supplier<String>> convert(String input) {
        return CompletableFuture.completedFuture(() -> {
            var delay = random.nextInt(1000);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return delay + "ms :: " +
                Arrays.toString(Base64.getEncoder().encode(input.getBytes(StandardCharsets.UTF_8)));
        });
    }
}
