import axios from 'axios'

export const api = axios.create({
	baseURL: 'http://localhost:8080/api/v1',
})

export const getHeader = () => {
	const token = localStorage.getItem('token')
	return {
		Authorization: `Bearer ${token}`,
		'Content-Type': 'application/json',
	}
}

export async function addRoom(photo, roomType, roomPrice) {
	const formData = new FormData()
	formData.append('photo', photo)
	formData.append('roomType', roomType)
	formData.append('roomPrice', roomPrice)

	const response = await api.post('/rooms', formData, {
		headers: {
			...getHeader(),
			'Content-Type': 'multipart/form-data',
		},
	})

	if (response.status === 201) {
		return true
	} else {
		return false
	}
}

export async function getRoomTypes() {
	try {
		const response = await api.get('/rooms/types')
		return response.data
	} catch (error) {
		throw new Error('Error fetching room types')
	}
}

export async function getAllRooms() {
	try {
		const result = await api.get('/rooms')
		return result.data
	} catch (error) {
		throw new Error('Error fetching rooms')
	}
}

export async function deleteRoom(roomId) {
	try {
		const result = await api.delete(`/rooms/${roomId}`, {
			headers: {
				...getHeader(),
				'Content-Type': 'multipart/form-data',
			},
		})
		return result.data
	} catch (error) {
		throw new Error(`Error deleting room ${error.message}`)
	}
}

export async function updateRoom(roomId, roomData) {
	const formData = new FormData()
	formData.append('roomType', roomData.roomType)
	formData.append('roomPrice', roomData.roomPrice)
	formData.append('photo', roomData.photo)
	const response = await api.put(`/rooms/${roomId}`, formData, {
		headers: {
			...getHeader(),
			'Content-Type': 'multipart/form-data',
		},
	})
	return response
}

export async function getRoomById(roomId) {
	try {
		const result = await api.get(`/rooms/${roomId}`)
		return result.data
	} catch (error) {
		throw new Error(`Error fetching room ${error.message}`)
	}
}

export async function bookRoom(roomId, booking) {
	try {
		const response = await api.post(`/bookings/${roomId}`, booking)
		return response.data
	} catch (error) {
		if (error.response && error.response.data) {
			throw new Error(error.response.data)
		} else {
			throw new Error(`Error booking room : ${error.message}`)
		}
	}
}

export async function getAllBookings() {
	try {
		const result = await api.get('/bookings', {
			headers: {
				...getHeader(),
				'Content-Type': 'multipart/form-data',
			},
		})
		return result.data
	} catch (error) {
		throw new Error(`Error fetching bookings : ${error.message}`)
	}
}

export async function getBookingByConfirmationCode(confirmationCode) {
	try {
		const result = await api.get(
			`/bookings/confirmation/${confirmationCode}`
		)
		return result.data
	} catch (error) {
		if (error.response && error.response.data) {
			throw new Error(error.response.data)
		} else {
			throw new Error(`Error finding booking : ${error.message}`)
		}
	}
}

export async function cancelBooking(bookingId) {
	try {
		const result = await api.delete(`/bookings/${bookingId}`, {
			headers: {
				...getHeader(),
				'Content-Type': 'multipart/form-data',
			},
		})
		return result.data
	} catch (error) {
		throw new Error(`Error cancelling booking : ${error.message}`)
	}
}

export async function getAvailableRooms(checkInDate, checkOutDate, roomType) {
	const result = await api.get(
		`/rooms/availableRooms?checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&roomType=${roomType}`
	)
	return result
}

export async function registerUser(registration) {
	try {
		const respone = await api.post('/auth/register', registration)
		return respone.data
	} catch (error) {
		if (error.respone && error.respone.data) {
			throw new Error(error.response.data)
		} else {
			throw new Error(`User registration error : ${error.message}`)
		}
	}
}

export async function loginUser(login) {
	try {
		const response = await api.post('/auth/login', login)
		if (response.status >= 200 && response.status < 300) {
			return response.data
		} else {
			return null
		}
	} catch (error) {
		console.error(error)
		return null
	}
}

export async function getUserProfile(userId, token) {
	try {
		const response = await api.get(`/users/${userId}`, {
			headers: getHeader(),
		})
		return response.data
	} catch (error) {
		throw new Error(error.message)
	}
}

export async function deleteUser(userId) {
	try {
		const response = await api.delete(`/users/${userId}`, {
			headers: getHeader(),
		})
		return response.data
	} catch (error) {
		return error.message
	}
}

export async function getUser(userId, token) {
	try {
		const respone = await api.get(`/users/${userId}`, {
			headers: getHeader(),
		})
		return respone.data
	} catch (error) {
		return error.message
	}
}
export async function getBookingsByUserId(userId, token) {
	try {
		const response = await api.get(`/bookings/user/${userId}`, {
			headers: getHeader(),
		})
		return response.data
	} catch (error) {
		console.error('Error fetching bookings:', error.message)
		throw new Error('Failed to fetch bookings')
	}
}
