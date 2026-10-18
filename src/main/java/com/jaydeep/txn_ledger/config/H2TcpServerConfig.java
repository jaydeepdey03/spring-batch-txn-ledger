package com.jaydeep.txn_ledger.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes the embedded H2 database over a TCP endpoint so external tools
 * (DBeaver, IntelliJ, h2 console, etc.) can connect remotely instead of only
 * in-process, e.g. jdbc:h2:tcp://<host>:<port>/./data/batchdb
 */
@Configuration
@ConditionalOnProperty(name = "app.h2.tcp.enabled", havingValue = "true", matchIfMissing = true)
public class H2TcpServerConfig {

    @Value("${app.h2.tcp.port:9092}")
    private int tcpPort;

    @Value("${app.h2.tcp.allow-others:true}")
    private boolean allowOthers;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        List<String> args = new ArrayList<>(List.of("-tcp", "-tcpPort", String.valueOf(tcpPort), "-ifNotExists"));
        if (allowOthers) {
            args.add("-tcpAllowOthers");
        }
        return Server.createTcpServer(args.toArray(new String[0]));
    }
}
