/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeInterface;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Jeryn
 */
public interface convertTime {
    
    LocalDateTime convert(LocalDateTime t, ZoneId x, ZoneId y);
    
}
