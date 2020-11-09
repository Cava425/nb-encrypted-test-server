package com.simit.data;

import com.simit.entity.AutoResult;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: ys xu
 * @Date: 2020/10/11 21:13
 */

@Repository
public class JdbcAutoResultRepository implements AutoResultRepository {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private JdbcTemplate jdbc;

    @Autowired
    public JdbcAutoResultRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @Override
    public List<AutoResult> findAll(String gasId) {
        String sql = "SELECT id, deviceid, batch_id, origin_data, command_code, command_name, control_code, successful_code, ts FROM t_auto_result_latest WHERE deviceid=?";
        try{
            return jdbc.queryForObject(sql, this::mapRowToAutoResults, gasId);
        }catch (EmptyResultDataAccessException e){

        }

        return new ArrayList<>();
    }

    @Override
    public List<AutoResult> findAllSuccessful(String gasId) {
        String sql = "SELECT id, deviceid, batch_id, origin_data, command_code, command_name, control_code, successful_code, ts FROM t_auto_result_latest WHERE deviceid=? AND successful_code=?";
        try{
            return jdbc.queryForObject(sql, this::mapRowToAutoResults, gasId, 1);
        }catch (EmptyResultDataAccessException e){

        }

        return new ArrayList<>();
    }




    private List<AutoResult> mapRowToAutoResults(ResultSet rs, int rowNum) throws SQLException {
        List<AutoResult> autoResults = new ArrayList<>();

        do {
            autoResults.add(new AutoResult(rs.getLong("id"),
                    rs.getString("deviceid"),
                    rs.getString("batch_id"),
                    rs.getString("origin_data"),
                    rs.getString("command_code"),
                    rs.getString("command_name"),
                    rs.getString("control_code"),
                    rs.getBoolean("successful_code"),
                    rs.getLong("ts")));
        }while (rs.next());

        return autoResults;
    }

    @Override
    public AutoResult save(AutoResult result) {
        String sql = "INSERT INTO t_auto_result_latest (deviceid, batch_id, origin_data, command_code, command_name, control_code, successful_code, ts) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, result.getGasId());
            ps.setString(2, result.getBatchId());
            ps.setString(3, result.getOriginData());
            ps.setString(4, result.getCommandCode());
            ps.setString(5, result.getCommandName());
            ps.setString(6, result.getControlCode());
            ps.setBoolean(7, result.getSuccessfulCode());
            ps.setLong(8, result.getCreateTime());
            return ps;
        }, holder);

        long id = Objects.requireNonNull(holder.getKey().longValue());
        result.setId(id);
        return result;
    }

    @Override
    public int[] delete(List<AutoResult> results) {
        String sql = "DELETE FROM t_auto_result_latest WHERE deviceid=?";

        List<Object[]> params = new ArrayList<>();
        for(AutoResult result : results){
            params.add(new Object[]{
                    result.getGasId()
            });
        }
        return jdbc.batchUpdate(sql, params);
    }


    @Override
    public int[] saveToSuccessful(List<AutoResult> results) {
        String sql = "INSERT INTO t_auto_result_successful (deviceid, batch_id, origin_data, command_code, command_name, control_code, successful_code, ts) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> params = new ArrayList<>();
        for(AutoResult result : results){
            params.add(new Object[]{
                    result.getId(),
                    result.getBatchId(),
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
    public int[] saveToFailed(List<AutoResult> results) {
        String sql = "INSERT INTO t_auto_result_failed (deviceid, batch_id, origin_data, command_code, command_name, control_code, successful_code, ts) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> params = new ArrayList<>();
        for(AutoResult result : results){
            params.add(new Object[]{
                    result.getGasId(),
                    result.getBatchId(),
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
