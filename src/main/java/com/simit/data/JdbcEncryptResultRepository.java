package com.simit.data;

import com.simit.entity.EncryptResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author: ys xu
 * @Date: 2020/10/28 9:15
 */

@Repository
public class JdbcEncryptResultRepository implements EncryptResultRepository{

    private JdbcTemplate jdbc;

    @Autowired
    public JdbcEncryptResultRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }


    @Override
    public EncryptResult findOne(String gasId) {
        String sql = "SELECT id, deviceid, batch_id, sampling_flg, export_ts, flag FROM t_encryption_function_verification WHERE deviceid=?  AND sampling_flg IS NOT NULL AND flag IS NULL ORDER BY export_ts DESC LIMIT 1";

        try{
            return jdbc.queryForObject(sql, this::mapToEncryptResult, gasId);
        }catch (EmptyResultDataAccessException e){

        }

        return null;
    }

    @Override
    public EncryptResult save(EncryptResult result) {
        String sql = "UPDATE t_encryption_function_verification SET flag=?, origin_data=?, ts=? WHERE deviceid=? AND batch_id=? AND export_ts=?";

        if(1 == jdbc.update(sql, result.getFlag(), result.getOriginData(), result.getTs(),
                result.getGasId(), result.getBatchId(), result.getExportTs())){
            return result;
        }

        return null;
    }


    private EncryptResult mapToEncryptResult(ResultSet rs, int rowNum) throws SQLException{
        return new EncryptResult(rs.getLong("id"),
                rs.getString("deviceid"),
                rs.getString("batch_id"),
                rs.getInt("sampling_flg"),
                rs.getLong("export_ts"),
                rs.getInt("flag"));
    }
}
