/*
 * Copyright 2019 Skullabs Contributors (https://github.com/skullabs)
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

package kos.injector;

import injector.*;
import io.vertx.core.logging.*;
import kos.core.*;
import lombok.*;

@ExposedAs(WebServerEventListener.class)
public class InjectorJobRunner implements WebServerEventListener {

    private final Logger logger = Kos.logger(this.getClass());
    private final Injector injector;

    public InjectorJobRunner(Injector injector){
        this.injector = injector;
    }

    @Override
    public void on(BeforeDeployEvent event) {
        val exitOnJobFailure = event.config().getBoolean("exit-on-job-failure", true);
        val jobs = injector.instancesExposedAs( Job.class );

        for (val job : jobs) {
            try {
                logger.debug("Starting job " + job);
                job.execute();
            } catch (Exception e) {
                logger.fatal("Failed to start job " + job, e);
                if ( exitOnJobFailure )
                    System.exit(1);
            }
        }
    }
}
