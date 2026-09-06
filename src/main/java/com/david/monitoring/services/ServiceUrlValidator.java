package com.david.monitoring.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

@Component
public class ServiceUrlValidator {

    public void validate(String value) {
        try {
            URI uri = URI.create(value);
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())
                    || uri.getHost() == null || uri.getUserInfo() != null || uri.getFragment() != null) {
                throw invalidUrl();
            }

            for (InetAddress address : InetAddress.getAllByName(uri.getHost())) {
                if (!isPublic(address)) throw invalidUrl();
            }
        } catch (IllegalArgumentException | UnknownHostException exception) {
            throw invalidUrl();
        }
    }

    private boolean isPublic(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
                || address.isSiteLocalAddress() || address.isMulticastAddress()) {
            return false;
        }

        byte[] bytes = address.getAddress();
        if (address instanceof Inet4Address) {
            int first = Byte.toUnsignedInt(bytes[0]);
            int second = Byte.toUnsignedInt(bytes[1]);
            return !(first == 0 || first == 10 || first == 127
                    || first == 100 && second >= 64 && second <= 127
                    || first == 169 && second == 254
                    || first == 172 && second >= 16 && second <= 31
                    || first == 192 && second == 168);
        }

        return !(address instanceof Inet6Address && (bytes[0] & 0xFE) == 0xFC);
    }

    private ResponseStatusException invalidUrl() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL must use a public HTTP or HTTPS address");
    }
}
