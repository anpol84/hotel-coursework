package com.example.lakesidehotel.service;

import com.example.lakesidehotel.exception.ResourceNotFoundException;
import com.example.lakesidehotel.model.Room;
import com.example.lakesidehotel.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class RoomServiceTest {

    @Test
    void addNewRoomTest() throws SQLException, IOException {
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        when(roomRepository.save(any())).thenReturn(new Room());
        roomService.addNewRoom(new MockMultipartFile("123", new byte[123]), "type1",
                new BigDecimal(1));
        verify(roomRepository).save(any());
    }

    @Test
    void getRoomTypesTest(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        when(roomRepository.findDistinctRoomTypes()).thenReturn(List.of("1", "2"));
        List<String> types = roomService.getRoomTypes();
        assertEquals(2, types.size());
        assertEquals("1", types.get(0));
        assertEquals("2", types.get(1));
    }

    @Test
    void getAllRoomsTest(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        Room room1 = new Room();
        Room room2 = new Room();
        room1.setId(1L);
        room2.setId(2L);
        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));
        List<Room> rooms = roomService.getAllRooms();
        assertEquals(2, rooms.size());
        assertEquals(1, rooms.get(0).getId());
        assertEquals(2, rooms.get(1).getId());
    }

    @Test
    void getRoomPhotoByRoomIdRoomNotFoundTest(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomPhotoByRoomId(1L));
    }

    @Test
    void getRoomPhotoByRoomIdGoodDataTest() throws SQLException {
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        Room room = new Room();
        room.setPhoto(new SerialBlob(new byte[]{1, 2, 3}));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        byte[] bytes =  roomService.getRoomPhotoByRoomId(1L);
        assertEquals(3, bytes.length);
        assertEquals(1, bytes[0]);
        assertEquals(2, bytes[1]);
        assertEquals(3, bytes[2]);
    }

    @Test
    void getRoomPhotoByRoomIdNullTest() throws SQLException {
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        Room room = new Room();
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        byte[] bytes =  roomService.getRoomPhotoByRoomId(1L);
        assertNull(bytes);
    }

    @Test
    void deleteRoomBadDataTest(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        roomService.deleteRoom(1L);
        verify(roomRepository).findById(1L);
        verify(roomRepository, never()).deleteById(1L);
    }

    @Test
    void deleteRoomGoodDataTest(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room()));
        doNothing().when(roomRepository).deleteById(1L);
        roomService.deleteRoom(1L);
        verify(roomRepository).findById(1L);
        verify(roomRepository).deleteById(1L);
    }

    @Test
    void updateRoomRoomNotFoundTest(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roomService.updateRoom(1L, "123",
                new BigDecimal(1), new byte[]{123}));
    }

    @Test
    void updateRoomGoodDataTest(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room()));
        when(roomRepository.save(any())).thenReturn(new Room());
        roomService.updateRoom(1L, "123", new BigDecimal(1), new byte[]{123});
        verify(roomRepository).findById(1L);
        verify(roomRepository).save(any());
    }

    @Test
    void getRoomById(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        Room room = new Room();
        room.setId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        Optional<Room> roomOptional = roomService.getRoomById(1L);
        assertTrue(roomOptional.isPresent());
        assertEquals(1, roomOptional.get().getId());
    }

    @Test
    void getAvailableRoomsTest(){
        RoomRepository roomRepository = mock(RoomRepository.class);
        RoomService roomService = new RoomService(roomRepository);
        Room room1 = new Room();
        room1.setId(1L);
        Room room2 = new Room();
        room2.setId(2L);
        when(roomRepository.findAvailableRoomsByDatesAndType(any(), any(), any())).thenReturn(List.of(room1, room2));
        List<Room> rooms = roomService.getAvailableRooms(LocalDate.MIN, LocalDate.MAX, "123");
        assertEquals(2, rooms.size());
        assertEquals(1, rooms.get(0).getId());
        assertEquals(2, rooms.get(1).getId());
    }
}
