package com.simit.data;

import com.simit.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: ys xu
 * @Date: 2020/10/11 21:13
 */

@Repository
public class JdbcResultRepository implements ResultRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcResultRepository.class);

    private JdbcTemplate jdbc;

    @Autowired
    public JdbcResultRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @Override
    public List<Result> findAll(String gasId) {
        String sql = "SELECT id, deviceid, batch_id, sampling_flag, origin_data, command_code, command_name, control_code, successful_code, ts FROM t_sampling_inspection_latest WHERE deviceid=?";
        try{
            return jdbc.queryForObject(sql, this::mapRowToAutoResults, gasId);
        }catch (EmptyResultDataAccessException e){
            logger.warn(e.toString());
        }

        return new ArrayList<>();
    }

    @Override
    public List<Result> findAllSuccessful(String gasId) {
        String sql = "SELECT id, deviceid, batch_id, sampling_flag, origin_data, command_code, command_name, control_code, successful_code, ts FROM t_sampling_inspection_latest WHERE deviceid=? AND successful_code=?";
        try{
            return jdbc.queryForObject(sql, this::mapRowToAutoResults, gasId, 1);
        }catch (EmptyResultDataAccessException e){
            logger.warn(e.toString());
        }

        return new ArrayList<>();
    }




    private List<Result> mapRowToAutoResults(ResultSet rs, int rowNum) throws SQLException {
        List<Result> results = new ArrayList<>();

        do {
            results.add(new Result(rs.getLong("id"),
                    rs.getString("deviceid"),
                    rs.getString("batch_id"),
                    rs.getString("sampling_flag"),
                    rs.getString("origin_data"),
                    rs.getString("command_code"),
                    rs.getString("command_name"),
                    rs.getString("control_code"),
                    rs.getString("successful_code"),
                    rs.getLong("ts")));
        }while (rs.next());

        return results;
    }

    @Override
    public Result save(Result result) {
        String sql = "INSERT into t_sampling_inspection_latest (deviceid, batch_id, sampling_flag, origin_data, command_code, command_name, control_code, successful_code, ts) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, result.getGasId());
            ps.setString(2, result.getBatchId());
            ps.setString(3, result.getSamplingFlag());
            ps.setString(4, result.getOriginData());
            ps.setString(5, result.getCommandCode());
            ps.setString(6, result.getCommandName());
            ps.setString(7, result.getControlCode());
            ps.setString(8, result.getSuccessfulCode());
            ps.setLong(9, result.getCreateTime());
            return ps;
        }, holder);

        long id = Objects.requireNonNull(holder.getKey().longValue());
        result.setId(id);
        return result;
    }

    @Override
    public int[] delete(List<Result> results) {
        String sql = "DELETE FROM t_sampling_inspection_latest WHERE deviceid=?";

        List<Object[]> params = new ArrayList<>();
        for(Result result : results){
            params.add(new Object[]{
                    result.getGasId()
            });
        }
        return jdbc.batchUpdate(sql, params);
    }


    @Override
    public int[] saveToSuccessful(List<Result> results) {
        String sql = "INSERT INTO t_sampling_inspection_success (deviceid, batch_id, sampling_flag, origin_data, command_code, command_name, control_code, successful_code, ts) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> params = new ArrayList<>();
        for(Result result : results){
            params.add(new Object[]{
                    result.getGasId(),
                    result.getBatchId(),
                    result.getSamplingFlag(),
                    result.getOriginData(),
                    result.getCommandCode(),
                    result.getCommandName(),
                    result.getControlCode(),
                    result.getSuccessfulCode(),
                    result.getCreateTime()
            });
        }
        return jdbc.batchUpdate(sql, params);
    }

    @Override
    public int[] saveToFailed(List<Result> results) {
        String sql = "INSERT INTO t_sampling_inspection_failed (deviceid, batch_id, sampling_flag, origin_data, command_code, command_name, control_code, successful_code, ts) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> params = new ArrayList<>();
        for(Result result : results){
            params.add(new Object[]{
                    result.getGasId(),
                    result.getBatchId(),
                    result.getSamplingFlag(),
                    result.getOriginData(),
                    result.getCommandCode(),
                    result.getCommandName(),
                    result.getControlCode(),
                    result.getSuccessfulCode(),
                    result.getCreateTime()
            });
        }
        return jdbc.batchUpdate(sql, params);
    }

}
