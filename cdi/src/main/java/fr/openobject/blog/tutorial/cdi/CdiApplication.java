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
package fr.openobject.blog.tutorial.cdi;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.openobject.blog.tutorial.cdi.api.Log;
import fr.openobject.blog.tutorial.cdi.bean.Manager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import java.util.UUID;

import static fr.openobject.blog.tutorial.cdi.api.Log.Type.AUDIT;
import static java.lang.System.exit;

public class CdiApplication {
    
    private final static Logger logger = LogManager.getLogger(CdiApplication.class);

    public static void main(String[] args) throws JsonProcessingException {

        try (final SeContainer container = SeContainerInitializer.newInstance().addBeanClasses(Manager.class).initialize()) {
            container.select(Manager.class).get().echo(UUID.randomUUID().toString());
        }
        Log log = new Log(AUDIT, "Application started");

        logger.info(new ObjectMessage(log));

        exit(0);
    }
}
