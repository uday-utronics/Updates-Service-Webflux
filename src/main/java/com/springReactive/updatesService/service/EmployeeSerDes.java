package com.springReactive.updatesService.service;

import com.springReactive.updatesService.model.EmployeeRequest;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EmployeeSerDes implements Serializer<EmployeeRequest>, Deserializer<EmployeeRequest> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String topic, EmployeeRequest data) {


        byte[] employeeName = data.getEmployeeName().getBytes(StandardCharsets.UTF_8);
        byte[] employeeCity = data.getEmployeeCity().getBytes(StandardCharsets.UTF_8);
        byte[] employeePhone = data.getEmployeePhone().getBytes(StandardCharsets.UTF_8);
//        byte[] javaExperience = data.getJavaExperience().t
//        byte[] springExperience
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + employeeName.length + 4 + employeeCity.length + 4 + employeePhone.length
                +8+8);
        buffer.putInt(data.getEmployeeId());
        buffer.putInt(employeeName.length);
        buffer.put(employeeName);
        buffer.putInt(employeeCity.length);
        buffer.put(employeeCity);
        buffer.putInt(employeePhone.length);
        buffer.put(employeePhone);
        buffer.putDouble(data.getJavaExperience());
        buffer.putDouble(data.getSpringExperience());
        return buffer.array();

    }

    @Override
    public EmployeeRequest deserialize(String topic, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int employeeId = buffer.getInt();

        byte[] name = new byte[buffer.getInt()];
        buffer.get(name);
        String employeeName = new String(name, StandardCharsets.UTF_8);

        byte[] city = new byte[buffer.getInt()];
        buffer.get(city);
        String employeeCity = new String(city, StandardCharsets.UTF_8);

        byte[] phone = new byte[buffer.getInt()];
        buffer.get(phone);
        String employeePhone = new String(phone, StandardCharsets.UTF_8);

        double javaExperience = buffer.getDouble();
        double springExperience = buffer.getDouble();


        EmployeeRequest employeeRequest = new EmployeeRequest(employeeId,employeeName,employeeCity,employeePhone,
                javaExperience,springExperience);
        return employeeRequest;
    }


    @Override
    public void close() {
        Serializer.super.close();
    }
}
