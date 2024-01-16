package ru.practicum.ewm.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatRequest;
import ru.practicum.ewm.ViewStats;

import java.sql.Timestamp;
import java.util.List;


@Component
@RequiredArgsConstructor
public class StatRepositoryImpl implements StatRepository {

    private final JdbcTemplate jdbcTemplate;
    private final StatMapper statMapper;


    @Override
    public void saveHit(EndpointHit endpointHit) {
        jdbcTemplate.update(" insert into stats (app, uri, ip, created) values (?, ?, ?, ?) ",
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                Timestamp.valueOf(endpointHit.getTimestamp()));
    }

    @Override
    public List<ViewStats> getViewStatsAll(StatRequest statRequest) {
        String query = " select app, uri, count(ip) as hits from stats where ( created >= ? and created <= ? ) ";

        if (!statRequest.getUris().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(" and uri in ('")
                    .append(String.join("', '", statRequest.getUris()))
                    .append("') ");
            query += stringBuilder;
        }

        query += " group by app, uri order by hits desc ";
        return jdbcTemplate.query(query, statMapper, statRequest.getStart(), statRequest.getEnd());
    }

    @Override
    public List<ViewStats> getViewStatsUnique(StatRequest statRequest) {
        String query = " select app, uri, count(distinct ip) as hits from stats where ( created >= ? and created <= ? ) ";

        if (!statRequest.getUris().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(" and uri in ('")
                    .append(String.join("', '", statRequest.getUris()))
                    .append("') ");
            query += stringBuilder;
        }

        query += " group by app, uri order by hits desc ";
        return jdbcTemplate.query(query, statMapper, statRequest.getStart(), statRequest.getEnd());
    }
}
