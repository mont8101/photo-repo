package photorepo.service.impl

import com.personal.ben.photorepo.model.Photo
import com.personal.ben.photorepo.repository.PhotoRepository
import com.personal.ben.photorepo.service.impl.PhotoServiceImpl
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import spock.lang.Specification
import spock.lang.Subject

class PhotoServiceImplSpec extends Specification {

    PhotoRepository photoRepository = Mock()

    @Subject
    PhotoServiceImpl photoService = new PhotoServiceImpl()

    def setup() {
        photoService.photoRepository = photoRepository
    }

    def "get() should return all photos without data"() {
        given: "a list of photos without data"
        def photos = [
                createPhotoWithoutData("1", "test1.jpg", "image/jpeg"),
                createPhotoWithoutData("2", "test2.png", "image/png")
        ]

        when: "getting all photos"
        def result = photoService.get()

        then: "repository is called correctly"
        1 * photoRepository.findAllWithoutData() >> photos

        and: "all photos are returned"
        result.size() == 2
        result.containsAll(photos)
    }

    def "get(id) should return photo with data"() {
        given: "a photo with data"
        def photoId = "test-id"
        def photo = createPhotoWithData(photoId, "test.jpg", "image/jpeg", "test data".bytes)

        when: "getting photo by id"
        def result = photoService.get(photoId)

        then: "repository is called with correct id"
        1 * photoRepository.findById(photoId) >> Optional.of(photo)

        and: "photo with data is returned"
        result == photo
        result.data == "test data".bytes
    }

    def "get(id) should return null when photo not found"() {
        given: "a non-existent photo id"
        def photoId = "non-existent"

        when: "getting photo by id"
        def result = photoService.get(photoId)

        then: "repository returns empty optional"
        1 * photoRepository.findById(photoId) >> Optional.empty()

        and: "null is returned"
        result == null
    }

    def "save() should create and save new photo"() {
        given: "photo data"
        def fileName = "test.jpg"
        def contentType = "image/jpeg"
        def data = "test image data".bytes
        def savedPhoto = createPhotoWithData("generated-id", fileName, contentType, data)

        when: "saving a photo"
        def result = photoService.save(fileName, contentType, data)

        then: "repository save is called"
        1 * photoRepository.save(_ as Photo) >> { Photo photo ->
            assert photo.fileName == fileName
            assert photo.contentType == contentType
            assert photo.data == data
            assert photo.id != null
            return savedPhoto
        }

        and: "saved photo is returned"
        result == savedPhoto
    }

    def "remove() should delete existing photo"() {
        given: "an existing photo"
        def photoId = "test-id"
        def photo = createPhotoWithData(photoId, "test.jpg", "image/jpeg", "data".bytes)

        when: "removing the photo"
        def result = photoService.remove(photoId)

        then: "photo is found and deleted"
        1 * photoRepository.findById(photoId) >> Optional.of(photo)
        1 * photoRepository.deleteById(photoId)

        and: "deleted photo is returned"
        result == photo
    }

    def "remove() should return null when photo not found"() {
        given: "a non-existent photo id"
        def photoId = "non-existent"

        when: "removing the photo"
        def result = photoService.remove(photoId)

        then: "photo is not found"
        1 * photoRepository.findById(photoId) >> Optional.empty()
        0 * photoRepository.deleteById(_)

        and: "null is returned"
        result == null
    }

    def "get(page, size) should return paginated photos"() {
        given: "paginated photo data"
        def page = 0
        def size = 5
        def photos = [
                createPhotoWithoutData("1", "test1.jpg", "image/jpeg"),
                createPhotoWithoutData("2", "test2.png", "image/png"),
                createPhotoWithoutData("3", "test3.gif", "image/gif")
        ]
        def photoPage = new PageImpl<>(photos)

        when: "getting paginated photos"
        def result = photoService.get(page, size)

        then: "repository is called with correct pagination parameters"
        1 * photoRepository.findAllPhotosWithoutData(_ as Pageable) >> { Pageable pageable ->
            assert pageable.pageNumber == page
            assert pageable.pageSize == size
            assert pageable.sort == Sort.by(Sort.Direction.DESC, "id")
            return photoPage
        }

        and: "paginated photos are returned"
        result.size() == 3
        result.containsAll(photos)
    }

    def "hasMorePhotos() should return true when more photos exist"() {
        given: "total count greater than current page"
        def page = 0
        def size = 5
        def totalCount = 10

        when: "checking if more photos exist"
        def result = photoService.hasMorePhotos(page, size)

        then: "repository count is called"
        1 * photoRepository.count() >> totalCount

        and: "true is returned"
        result == true
    }

    def "hasMorePhotos() should return false when no more photos exist"() {
        given: "total count equal to current page"
        def page = 1
        def size = 5
        def totalCount = 10

        when: "checking if more photos exist"
        def result = photoService.hasMorePhotos(page, size)

        then: "repository count is called"
        1 * photoRepository.count() >> totalCount

        and: "false is returned"
        result == false
    }

    def "hasMorePhotos() should return false when total count is less than current page"() {
        given: "total count less than current page"
        def page = 2
        def size = 5
        def totalCount = 8

        when: "checking if more photos exist"
        def result = photoService.hasMorePhotos(page, size)

        then: "repository count is called"
        1 * photoRepository.count() >> totalCount

        and: "false is returned"
        result == false
    }

    def "getTotalCount() should return repository count"() {
        given: "a total count"
        def totalCount = 42

        when: "getting total count"
        def result = photoService.getTotalCount()

        then: "repository count is called"
        1 * photoRepository.count() >> totalCount

        and: "count is returned"
        result == totalCount
    }

    def "searchByFileName() should return photos matching filename"() {
        given: "photos with matching filenames"
        def searchTerm = "vacation"
        def photos = [
                createPhotoWithoutData("1", "vacation1.jpg", "image/jpeg"),
                createPhotoWithoutData("2", "vacation2.png", "image/png")
        ]

        when: "searching by filename"
        def result = photoService.searchByFileName(searchTerm)

        then: "repository search is called"
        1 * photoRepository.findByFileNameContainingIgnoreCase(searchTerm) >> photos

        and: "matching photos are returned"
        result == photos
    }

    def "getByContentType() should return photos with matching content type"() {
        given: "photos with matching content type"
        def contentType = "image/jpeg"
        def photos = [
                createPhotoWithoutData("1", "test1.jpg", contentType),
                createPhotoWithoutData("2", "test2.jpg", contentType)
        ]

        when: "getting photos by content type"
        def result = photoService.getByContentType(contentType)

        then: "repository query is called"
        1 * photoRepository.findByContentType(contentType) >> photos

        and: "matching photos are returned"
        result == photos
    }

    // Helper methods
    private Photo createPhotoWithoutData(String id, String fileName, String contentType) {
        def photo = new Photo()
        photo.id = id
        photo.fileName = fileName
        photo.contentType = contentType
        return photo
    }

    private Photo createPhotoWithData(String id, String fileName, String contentType, byte[] data) {
        def photo = createPhotoWithoutData(id, fileName, contentType)
        photo.data = data
        return photo
    }
}