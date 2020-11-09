package com.simit.data;

import com.simit.entity.GasAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author: ys xu
 * @Date: 2020/10/27 16:01
 */

@Repository
public class JdbcGasAddressRepository implements GasAddressRepository {

    private JdbcTemplate jdbc;

    @Autowired
    public JdbcGasAddressRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @Override
    public GasAddress findOne() {
        String sql = "SELECT id, fqdn, encryption_udp, auto_udp_address, ts FROM t_domain_address ORDER BY ts DESC LIMIT 1";
        try{
            return jdbc.queryForObject(sql, this::mapToGasAddress);
        }catch (DataAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    private GasAddress mapToGasAddress(ResultSet rs, int rowNum) throws SQLException {
        return new GasAddress(rs.getLong("id"),
                rs.getString("fqdn"),
                rs.getString("encryption_udp"),
                rs.getString("auto_udp_address"),
                rs.getLong("ts"));
    }
}
