import React from "react";
import {
  Card,
  CardTitle,
  Row,
  Col
} from "reactstrap";
import { useNavigate } from "react-router-dom";
import { t } from "translations/translate";

/**
* Renders the admin dashboard component, which displays a set of cards for managing various aspects of the application.
* The cards include options for user management, competition management, team management, playground management, and robot confirmation.
* Each card is clickable and navigates the user to the corresponding admin management page.
*/
function AdminDashboard() {
  const navigate = useNavigate();

  const handleNavigate = (path) => {
    navigate(path);
  };

  return (
    <div className="content">
      <Row style={{ justifyContent: 'center' }}> 
        {/* Card for User Management */}
        <Col md="4" style={{ display: 'flex', justifyContent: 'center' }} className="text-center p-3">
          <Card
            onClick={() => handleNavigate('/admin/user-management')}
            style={{
              cursor: 'pointer',
              width: '300px',
              height: '300px',
              margin: '10px',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              flexDirection: 'column'
            }}
            className="text-white"
          >
            <CardTitle tag="h4" style={{ fontSize: '1.5em' }}>
              <i className="tim-icons icon-single-02 mr-2" /> 
              {t("manageUser")}
            </CardTitle>
          </Card>
        </Col>
        {/* Card for Competition Management */}
        <Col md="4" style={{ display: 'flex', justifyContent: 'center' }} className="text-center p-3">
          <Card
            onClick={() => handleNavigate('/admin/competition-management')}
            style={{
              cursor: 'pointer',
              width: '300px',
              height: '300px',
              margin: '10px',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              flexDirection: 'column'
            }}
            className="text-white"
          >
            <CardTitle tag="h4" style={{ fontSize: '1.5em' }}>
              <i className="tim-icons icon-trophy mr-2" /> 
              {t("manageComp")}
            </CardTitle>
          </Card>
        </Col>
        {/* New Card for Team Management */}
        <Col md="4" style={{ display: 'flex', justifyContent: 'center' }} className="text-center p-3">
          <Card
            onClick={() => handleNavigate('/admin/all-teams')}
            style={{
              cursor: 'pointer',
              width: '300px',
              height: '300px',
              margin: '10px',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              flexDirection: 'column'
            }}
            className="text-white"
          >
            <CardTitle tag="h4" style={{ fontSize: '1.5em' }}>
              <i className="tim-icons icon-molecule-40 mr-2" /> 
              {t("teamList")}
            </CardTitle>
          </Card>
        </Col>
        {/* Card for Playground Management */}
        <Col md="4" style={{ display: 'flex', justifyContent: 'center' }} className="text-center p-3">
          <Card
            onClick={() => handleNavigate('/admin/playground-management')}
            style={{
              cursor: 'pointer',
              width: '300px',
              height: '300px',
              margin: '10px',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              flexDirection: 'column'
            }}
            className="text-white"
          >
            <CardTitle tag="h4" style={{ fontSize: '1.5em' }}>
              <i className="tim-icons icon-app mr-2" /> 
              {t("managePg")}
            </CardTitle>
          </Card>
        </Col>
        {/* Card for Robot Confirmation */}
        <Col md="4" style={{ display: 'flex', justifyContent: 'center' }} className="text-center p-3">
          <Card
            onClick={() => handleNavigate('/admin/robot-confirmation')}
            style={{
              cursor: 'pointer',
              width: '300px',
              height: '300px',
              margin: '10px',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              flexDirection: 'column'
            }}
            className="text-white"
          >
            <CardTitle tag="h4" style={{ fontSize: '1.5em' }}>
              <i className="tim-icons icon-vector mr-2" /> 
              {t("robotConf")}
            </CardTitle>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default AdminDashboard;
