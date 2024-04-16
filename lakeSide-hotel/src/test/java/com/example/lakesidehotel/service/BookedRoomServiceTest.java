package com.example.lakesidehotel.service;

import com.example.lakesidehotel.exception.InvalidBookingRequestException;
import com.example.lakesidehotel.exception.ResourceNotFoundException;
import com.example.lakesidehotel.model.BookedRoom;
import com.example.lakesidehotel.model.Room;
import com.example.lakesidehotel.repository.BookRoomRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class BookedRoomServiceTest {

    @Test
    void getAllBookingsByRoomIdTest(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        when(bookRoomRepository.findByRoomId(1L)).thenReturn(List.of(new BookedRoom()));
        BookedRoomService bookedRoomService = new BookedRoomService(bookRoomRepository, roomService);
        bookedRoomService.getAllBookingsByRoomId(1L);
        verify(bookRoomRepository).findByRoomId(1L);
    }

    @Test
    void cancelBookingTest(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        doNothing().when(bookRoomRepository).deleteById(1L);
        BookedRoomService bookedRoomService = new BookedRoomService(bookRoomRepository, roomService);
        bookedRoomService.cancelBooking(1L);
        verify(bookRoomRepository).deleteById(1L);
    }

    @Test
    void saveBookingBadStartAndEndDateTest(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        BookedRoomService bookedRoomService = new BookedRoomService(bookRoomRepository, roomService);
        BookedRoom bookedRoom = new BookedRoom(1L, LocalDate.MAX, LocalDate.MIN,
                "", "", 1, 1,
                1, "", new Room());
        assertThrows(InvalidBookingRequestException.class, () -> bookedRoomService.saveBooking(1L, bookedRoom));
    }

    @Test
    void saveBookingBadDateTest(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        BookedRoomService bookedRoomService = spy(new BookedRoomService(bookRoomRepository, roomService));
        Room room = new Room();
        room.addBooking(new BookedRoom(1L, LocalDate.MIN, LocalDate.MAX,
                "", "", 1, 1,
                1, "", room));
        when(roomService.getRoomById(1L)).thenReturn(Optional.of(room));
        BookedRoom bookedRoom = new BookedRoom(1L, LocalDate.MIN, LocalDate.MAX,
                "", "", 1, 1,
                1, "", room);
        assertThrows(InvalidBookingRequestException.class, () -> bookedRoomService.saveBooking(1L, bookedRoom));
    }

    @Test
    void saveBookingGoodDateTest(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        BookedRoomService bookedRoomService = spy(new BookedRoomService(bookRoomRepository, roomService));
        Room room = new Room();
        when(roomService.getRoomById(1L)).thenReturn(Optional.of(room));
        BookedRoom bookedRoom = new BookedRoom(1L, LocalDate.MIN, LocalDate.MAX,
                "", "", 1, 1,
                1, "123", room);
        when(bookRoomRepository.save(bookedRoom)).thenReturn(bookedRoom);
        bookedRoomService.saveBooking(1L, bookedRoom);
        verify(roomService).getRoomById(1L);
        verify(bookRoomRepository).save(bookedRoom);
    }

    @Test
    void findByBookingConfirmationCodeBadDataTest(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        BookedRoomService bookedRoomService = new BookedRoomService(bookRoomRepository, roomService);
        when(bookRoomRepository.findByBookingConfirmationCode("123")).thenThrow(new ResourceNotFoundException("Error"));
        assertThrows(ResourceNotFoundException.class, () -> bookedRoomService.findByBookingConfirmationCode("123"));
    }

    @Test
    void findByBookingConfirmationCodeGoodDataTest(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        BookedRoomService bookedRoomService = new BookedRoomService(bookRoomRepository, roomService);
        BookedRoom bookedRoom = new BookedRoom(1L, LocalDate.MIN, LocalDate.MAX,
                "", "", 1, 1,
                1, "123", new Room());
        when(bookRoomRepository.findByBookingConfirmationCode("123")).thenReturn(Optional.of(bookedRoom));
        BookedRoom bookedRoom1 = bookedRoomService.findByBookingConfirmationCode("123");
        assertEquals("123", bookedRoom1.getBookingConfirmationCode());
    }

    @Test
    void getAllBookingTest(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        BookedRoomService bookedRoomService = new BookedRoomService(bookRoomRepository, roomService);
        BookedRoom bookedRoom1 = new BookedRoom(1L, LocalDate.MIN, LocalDate.MAX,
                "", "", 1, 1,
                1, "123", new Room());
        BookedRoom bookedRoom2 = new BookedRoom(1L, LocalDate.MIN, LocalDate.MAX,
                "", "", 1, 1,
                1, "245", new Room());
        when(bookRoomRepository.findAll()).thenReturn(List.of(bookedRoom1, bookedRoom2));
        List<BookedRoom> rooms = bookedRoomService.getAllBookings();
        assertEquals(2, rooms.size());
        assertEquals("123", rooms.get(0).getBookingConfirmationCode());
        assertEquals("245", rooms.get(1).getBookingConfirmationCode());
    }

    @Test
    void getBookingsByUserEmail(){
        BookRoomRepository bookRoomRepository = mock(BookRoomRepository.class);
        RoomService roomService = mock(RoomService.class);
        BookedRoomService bookedRoomService = new BookedRoomService(bookRoomRepository, roomService);
        BookedRoom bookedRoom1 = new BookedRoom(1L, LocalDate.MIN, LocalDate.MAX,
                "", "", 1, 1,
                1, "123", new Room());
        BookedRoom bookedRoom2 = new BookedRoom(1L, LocalDate.MIN, LocalDate.MAX,
                "", "", 1, 1,
                1, "245", new Room());
        when(bookRoomRepository.findByGuestEmail("123")).thenReturn(List.of(bookedRoom1, bookedRoom2));
        List<BookedRoom> rooms = bookedRoomService.getBookingsByUserEmail("123");
        assertEquals(2, rooms.size());
        assertEquals("123", rooms.get(0).getBookingConfirmationCode());
        assertEquals("245", rooms.get(1).getBookingConfirmationCode());
    }
}
