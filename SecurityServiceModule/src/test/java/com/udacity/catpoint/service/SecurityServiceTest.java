package com.udacity.catpoint.service;

import com.udacity.catpoint.data.*;
import com.udacity.catpoint.imageService.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    private SecurityService securityService;

    @Mock
    private SecurityRepository securityRepository;

    @Mock
    private Sensor sensor;

    @Mock
    private ImageService imageService;

    @Mock
    private BufferedImage image;

    @BeforeEach
    void init(){
        securityService = new SecurityService(securityRepository,imageService);
    }

    @Test
    public void alamStatus_armedAndactived_pendingAlarm(){
        when(sensor.getActive()).thenReturn(false);
        when(securityService.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityService.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

        securityService.changeSensorActivationStatus(sensor,true);
        verify(securityRepository,times(1)).setAlarmStatus(eq(AlarmStatus.PENDING_ALARM));
    }

    @Test
    public void alamStatus_armedAndactived_alarm(){
        when(sensor.getActive()).thenReturn(false);
        when(securityService.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityService.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor,true);
        verify(securityRepository,times(1)).setAlarmStatus(eq(AlarmStatus.ALARM));
    }

    @Test
    public void alamStatus_pendingAndinactive_Noalarm(){
        when(sensor.getActive()).thenReturn(true);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor,false);
        verify(securityRepository,times(1)).setAlarmStatus(eq(AlarmStatus.NO_ALARM));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    public void alamStatus_activeAlarm_NochangeState(boolean active){
        when(sensor.getActive()).thenReturn(!active);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(sensor,active);
        verify(securityRepository,times(0)).setAlarmStatus(any());
    }

    @Test
    public void alamStatus_sensorActiveWhileActiveAndPendingAlarm_alarm(){
        when(sensor.getActive()).thenReturn(true);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor,true);
        verify(securityRepository,times(1)).setAlarmStatus(eq(AlarmStatus.ALARM));
    }

    @Test
    public void alamStatus_sensorDeactiveWhileInactive_NoChangeAlarm(){
        when(sensor.getActive()).thenReturn(false);

        securityService.changeSensorActivationStatus(sensor,false);
        verify(securityRepository,times(0)).setAlarmStatus(any());
    }

    @Test
    public void alamStatus_catDetectedAndArmed_alarm(){
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(image,50.0f)).thenReturn(true);

        securityService.processImage(image);

        verify(securityRepository,times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    public void alamStatus_catNotDetectedAndSensorInactive_Noalarm(){
        when(imageService.imageContainsCat(image,50.0f)).thenReturn(false);
        InOrder inOrder = Mockito.inOrder(securityRepository);

        securityService.processImage(image);
        inOrder.verify(securityRepository,times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);

        when(sensor.getActive()).thenReturn(false);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor,true);
        inOrder.verify(securityRepository,times(1)).setAlarmStatus(any());
    }

    @Test
    public void alamStatus_disarmed_NoAlarm(){
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }
/*
    @Test
    public void sensorActivation_armed_AllsensorsDisable(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        verify(securityRepository,atLeast(1)).updateSensor(sensor);
    }*/
/*
    @ParameterizedTest
    @MethodSource("differentAlarmStatus")
    public void alarmStatus_armedHomeWhileShowsCat_alarm(ArmingStatus alarmStatus){
        InOrder inOrder = Mockito.inOrder(securityRepository);
        when(imageService.imageContainsCat(image,50.0f)).thenReturn(true);
        when(securityRepository.getArmingStatus()).thenReturn(alarmStatus);

        securityService.processImage(image);
        inOrder.verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        inOrder.verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
*/
    private static Stream<Arguments> differentAlarmStatus(){
        return Stream.of(
                Arguments.of(
                        ArmingStatus.DISARMED,
                        ArmingStatus.ARMED_AWAY
                )
        );
    }
}