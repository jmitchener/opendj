/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2016 ForgeRock AS.
 */
package org.forgerock.opendj.rest2ldap.authz;

import static org.forgerock.opendj.rest2ldap.Rest2LDAP.asResourceException;
import static org.forgerock.util.Utils.closeSilently;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicReference;

import org.forgerock.http.protocol.Response;
import org.forgerock.http.protocol.Status;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.util.promise.NeverThrowsException;
import org.forgerock.util.promise.Promise;
import org.forgerock.util.promise.Promises;

final class Utils {

    private Utils() { }

    static Runnable close(final AtomicReference<? extends Closeable> holder) {
        return new Runnable() {
            @Override
            public void run() {
                closeSilently(holder.get());
            }
        };
    }

    static Promise<Response, NeverThrowsException> asErrorResponse(final Throwable t) {
        final ResourceException e = asResourceException(t);
        final Response response = new Response()
                .setStatus(Status.valueOf(e.getCode()))
                .setEntity(e.toJsonValue().getObject());
        if (response.getStatus() == Status.UNAUTHORIZED) {
            response.getHeaders().put("WWW-Authenticate", "Basic");
        }
        return Promises.newResultPromise(response);
    }
}
