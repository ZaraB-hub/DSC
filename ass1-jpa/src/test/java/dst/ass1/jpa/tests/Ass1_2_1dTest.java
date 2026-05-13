package dst.ass1.jpa.tests;

import dst.ass1.grading.GitHubClassroomGrading;
import dst.ass1.grading.LocalGradingClassRule;
import dst.ass1.grading.LocalGradingRule;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ass1_2_1dTest extends Ass1_TestBase {
    @ClassRule
    public static LocalGradingClassRule afterAll = new LocalGradingClassRule();

    @Rule
    public LocalGradingRule grading = new LocalGradingRule();

    private ITripDAO tripDAO;

    @Before
    public void setUp() throws Exception {
        tripDAO = daoFactory.createTripDAO();
    }

    @GitHubClassroomGrading(maxScore = 3)
    @Test(timeout = 2000)
    public void findTripsByState_forOneApproachingTrip_returnsOne() throws Exception {
        List<ITrip> trips = tripDAO.findByStatus(TripState.APPROACHING);
        assertEquals(1, trips.size());
        assertEquals(trips.get(0).getId(), this.testData.trip7Id);
    }

    @GitHubClassroomGrading(maxScore = 3)
    @Test(timeout = 2000)
    public void findTripsByState_forFiveCompletedTrips_returnsSix() throws Exception {
        List<ITrip> trips = tripDAO.findByStatus(TripState.COMPLETED);
        assertEquals(6, trips.size());
    }
}
