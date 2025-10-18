import React, { useState, useEffect } from "react";
import {
  Button,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  FormGroup,
  Form,
  FormFeedback,
  Input,
  Row,
  Col,
} from "reactstrap";
import { useUser } from "contexts/UserContext";
import { validateBirth, minAge, maxAge } from "./Register";
import { t } from "translations/translate";

function UserProfile() {
  const [userData, setUserData] = useState({
    id: '',
    uuid: '',
    name: '',
    surname: '',
    email: '',
    birthDate: '',
    roles: [],
    teamID: ''
  });
  const [initialUserData, setInitialUserData] = useState({});
  const { token, tokenExpired } = useUser();
  const [errors, setErrors] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}api/user/info`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (tokenExpired(response.status)) { return; }

        if (response.ok) {
          const jsonResponse = await response.json();
          setUserData(jsonResponse.data);
          setInitialUserData(jsonResponse.data); // Store initial data for comparison
        } else {
          console.error("Failed to fetch user data:", response.status);
        }
      } catch (error) {
        console.error("Error fetching user data:", error);
      }
    };

    fetchData();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    let newErrors = { ...errors };

    if (name === 'birthDate') {
      // validate date of birth
      const val = validateBirth(value);
      if (!val) {
        newErrors.birthDate = t("invalidAge");
      } else if (val === "younger") {
        newErrors.birthDate = t("tooYoung", { age: minAge });
      } else if (val === "older") {
        newErrors.birthDate = t("tooOld", { age: maxAge });
      } else {
        delete newErrors.birthDate;
      }
    }

    setErrors(newErrors);
  }

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent form submission reload

    // check for mistakes
    if (errors.birthDate) {
      alert(t("regMistakes"));
      return;
    }

    // Check for actual changes and no empty required inputs
    if (userData.name && userData.surname && userData.birthDate &&
      (userData.name !== initialUserData.name ||
        userData.surname !== initialUserData.surname ||
        userData.birthDate !== initialUserData.birthDate)) {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}api/user/edit`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            name: userData.name,
            surname: userData.surname,
            birthDate: userData.birthDate
          })
        });
        if (tokenExpired(response.status)) { return; }

        if (!response.ok) throw new Error(t("userUpdateFail"));

        const result = await response.json();
        if (result.data === "success") {
          // console.log('Uložení se podařilo:', result);
          alert(t("dataSaved"));
        } else {
          alert(t("userUpdateFail"));
        }
      } catch (error) {
        console.error('Update selhal:', error);
        alert(t("dataSaveFail"));
      }
    } else {
      alert(t("noChanges"));
    }
  };

  return (
    <>
      <div className="content">
        <Row>
          <Col md="8">
            <Card>
              <CardHeader>
                <h5 className="title">{t("infoEdit")}</h5>
              </CardHeader>
              <CardBody>
                <Form onSubmit={handleSubmit}>
                  <Row>
                    <Col className="pr-md-1" md="6">
                      <FormGroup>
                        <label>{t("name")}</label>
                        <Input
                          value={userData.name}
                          placeholder={t("name")}
                          type="text"
                          onChange={(e) => setUserData({ ...userData, name: e.target.value })}
                          required
                        />
                      </FormGroup>
                    </Col>
                    <Col className="pl-md-1" md="6">
                      <FormGroup>
                        <label>{t("surname")}</label>
                        <Input
                          value={userData.surname}
                          placeholder={t("surname")}
                          type="text"
                          onChange={(e) => setUserData({ ...userData, surname: e.target.value })}
                          required
                        />
                      </FormGroup>
                    </Col>
                  </Row>
                  <Row>
                    <Col md="12">
                      <FormGroup>
                        <label>{t("mail")}</label>
                        <Input
                          value={userData.email}
                          placeholder={t("mail")}
                          type="email"
                          onChange={(e) => setUserData({ ...userData, email: e.target.value })}
                          disabled
                        />
                      </FormGroup>
                    </Col>
                  </Row>
                  <Row>
                    <Col md="6">
                      <FormGroup>
                        <label>{t("birthDate")}</label>
                        <Input
                          invalid={!!errors.birthDate}
                          value={userData.birthDate}
                          type="date"
                          name="birthDate"
                          onChange={(e) => setUserData({ ...userData, birthDate: e.target.value })}
                          onBlur={handleChange}
                          required
                        />
                        {errors.birthDate && <FormFeedback>{errors.birthDate}</FormFeedback>}
                      </FormGroup>
                    </Col>

                  </Row>
                  <CardFooter>
                    <Button className="btn-fill" color="primary" type="submit">
                      {t("save")}
                    </Button>
                  </CardFooter>
                </Form>
              </CardBody>
            </Card>
          </Col>
          <Col md="4">
            <Card className="card-user">
              <CardBody>
                <div className="author">
                  <a href="#pablo" onClick={(e) => e.preventDefault()}>
                    <img
                      alt="..."
                      className="avatar"
                      src={require("assets/img/profile-picture.png")}
                    />
                    <h5 className="title">{userData.name + ' ' + userData.surname}</h5>
                  </a>
                  <p className="description">
                    {userData.roles.map(role => role.name).join(', ')}
                  </p>
                </div>
              </CardBody>
            </Card>
          </Col>
        </Row>
      </div>
    </>
  );
}

export default UserProfile;