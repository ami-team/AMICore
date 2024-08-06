#!/bin/bash

########################################################################################################################
########################################################################################################################

THIS_SCRIPT=${BASH_SOURCE[0]:-$0}

while [[ -n $(readlink "${THIS_SCRIPT}") ]]
do
  THIS_SCRIPT=$(readlink "${THIS_SCRIPT}")
done

AMI_HOME=$(cd $(dirname "${THIS_SCRIPT}") && pwd)/..

########################################################################################################################
########################################################################################################################

source "${AMI_HOME}/bin/setenv.sh"

########################################################################################################################
########################################################################################################################

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

########################################################################################################################

function _line()
{
  echo -e "${BLUE}-----------------------------------------------------------------------------${NC}"
}

########################################################################################################################

function _box()
{
  _line
  echo -e "${BLUE}- $1${NC}"
  _line
}

########################################################################################################################

function _ok()
{
  echo -e "                                                                       [${GREEN}OKAY${NC}]"
  #### #
}

########################################################################################################################

function _err()
{
  echo -e "                                                                       [${RED}ERR.${NC}]"
  exit 1
}

########################################################################################################################
########################################################################################################################

if [ "${AMI_RESTART}" != '0' ]
then
  _box "Stopping AMI"

  "${AMI_HOME}/bin/ami.sh" stop &> /dev/null

  _ok
fi

########################################################################################################################
########################################################################################################################

_box "Updating '\${AMI_HOME}/webapps/AMI.war'"

(
  rm -fr "${AMI_HOME}/webapps/AMI/"

  mv "${AMI_HOME}/webapps/AMI.war" "${AMI_HOME}/webapps/AMI.war.old"

  curl -L https://repo.ami-ecosystem.in2p3.fr/releases/net/hep/ami/AMICoreWeb/1.0.0/AMICoreWeb-1.0.0.war > "${AMI_HOME}/webapps/AMI.war"

  _ok

) || _err

########################################################################################################################
########################################################################################################################

_box "Generating '\${AMI_HOME}/certs/ami.jks'"

(
  ################
  # DOWNLOAD     #
  ################

  rm -fr "${AMI_HOME}/certs/temp/"
  mkdir -p "${AMI_HOME}/certs/temp/"

  IGTF_BASE_URL=https://dist.igtf.net/distribution/current

  IGTF_VERSION=$(curl --silent ${IGTF_BASE_URL}/version.txt)
  IGTF_VERSION=${IGTF_VERSION//[^0-9\.]}
  curl --silent \
       -o "${AMI_HOME}/certs/temp/igtf-preinstalled-bundle-classic-${IGTF_VERSION}.tar.gz" \
       -L "${IGTF_BASE_URL}/accredited/igtf-preinstalled-bundle-classic-${IGTF_VERSION}.tar.gz"

  ################
  # EXTRACT      #
  ################

  tar xzf "${AMI_HOME}/certs/temp/igtf-preinstalled-bundle-classic-${IGTF_VERSION}.tar.gz" -C "${AMI_HOME}/certs/temp/"

  ################
  # PROCESS      #
  ################

  cat > "${AMI_HOME}/certs/temp/ami.b64" << EOF
/u3+7QAAAAIAAAACAAAAAgAGYW1pLWNhAAABWf42MLQABVguNTA5AAAFkDCCBYww
ggN0oAMCAQICBgFObX+oLTANBgkqhkiG9w0BAQ0FADBtMQswCQYDVQQGEwJGUjER
MA8GA1UEBxMIR3Jlbm9ibGUxDTALBgNVBAoTBENOUlMxETAPBgNVBAsTCExQU0Mt
QU1JMSkwJwYDVQQDEyBBTUkgUm9vdCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTAe
Fw0xNTA3MDgxMTQ5MTlaFw0zMDA3MDgxMTQ5MTlaMG0xCzAJBgNVBAYTAkZSMREw
DwYDVQQHEwhHcmVub2JsZTENMAsGA1UEChMEQ05SUzERMA8GA1UECxMITFBTQy1B
TUkxKTAnBgNVBAMTIEFNSSBSb290IENlcnRpZmljYXRpb24gQXV0aG9yaXR5MIIC
IjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAhgzKF2HeQaEWVB5qJYdBVdBt
crILUBJHnuZ7GHrBkjnruzCuE4REYHzW2K4GQu4iWZWkXJVshafZDCeiubludL4I
694h5nVVX4lcuKCOm3g4HsU1m3OzKnTmVas5bjIt7jaQupnvKPsXl4dTSs4SkFkM
POf3D2E11Kx/6sKAbToYuWW+kU0w8O1mctgV1rf9bzRm9AlniNZcfmrSe0PhV9v9
pv5n6MN52tIieOKZpGNO4ODeqfR8gJKEL9YKUvxG6MztQPP4KmOXd94MJM9SsdeV
qGquA0Q0Uod/9B1kTplHlQuvrtdsxFB5oBnaUAWYeZlPryUehN81q+Mams1iqkGN
l64KOKcOkilfYrbCUHJRBVwi6WIsocM1fqw5hlg/vt/rbjuy1xHomOlJ1WwJO4dq
jOxK8fLcUt80RtexzUP14zXFjzjJcB3ADg26sMRJfsS60WvdklDniZzQsTJkGRM5
EUM32T7Fr6OEhu/rWv91R3RftafKkOd1Z1YT4G/icZn32AdMOdVywmnj66Ex5S+7
w9EXwr3Q3Kq/oPdbi55BEvyGdWS2BTCpblh8WMk0hjhqt0xXHPAgytVwqxGjuvk8
gWlQfyoMJliqKB4oIksVFf8yPDCo/E/oTDlIOiAsdUErhNHCl+hLoq3sezdO27VD
ZcBFnZCMCzbhVc/ZJ9sCAwEAAaMyMDAwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4E
FgQUdjUiQTRy1EzoknmiJdg1T3U1nDcwDQYJKoZIhvcNAQENBQADggIBACXIQcHt
kLypPTqRtDRRjpANkHrYpXU3//n/0EScb87YszFER/jNxwfTxjy5q+0EsNbinZqH
y0F3lVitZYArGBo9Qho7MhkUPd+QU25q3hHKekhmG5S9gensxDtl+GDXb6s3buRG
usYc6QRxFkx7qmsU8cw2NA9f+xReM96tfE4GqjyMYiFWFb3tGLaAN6TPbp1I3uil
yyKre/he/BB0pwt4GF6HLBEter/f82hXubvEFPYV7MHpWhV6NRtSL8gbWRlIq70B
Wj9wmIhzShnGBW18M/DKjYJ31zFs2sVZXXjBqnswZEZ4no1caYyvmgJ4ZBAV8/zX
/KkY94KmPyg6jwp9oQUZWhEyf8PrxJ1ntY1BV5etzJIykaevVjzpBlcdQrdvKMKs
DMFH4spCHu0n7gf9wrQe1JbMJ+oYDnSvYlm81xfAnsHlgD1nd5Q/HbljvJewjMlO
ABALLOjiem9P3u9O1r4s/lwouMkmrbkxF3XRLDsn60ErYe3s3EOevkAMYQcnziVR
o5xaK7LFX1+HeeMbTpNr7bx0H5e6jml+Lnb1ieGKidjNnd4/da9WkbQOpKXycyvd
85yiig/PD/LfEWpZSABsAQSy3eHu/iDALa9rJ4wIe+nu1s7zjVND/vtG9S682/1r
e1U1HErQ74LrOlL3idev5plc/avN7LZOpRNrAAAAAQADYW1pAAABWf42MJgAAAUB
MIIE/TAOBgorBgEEASoCEQEBBQAEggTpyot+4eMBlsmvSQXh1iVKnuD7hGNTJeyv
Gr0hwARzydRtNb1P5AOOcUnkSGAdl6zVEcWbwfWsZ41oTDZLUOHVaMUkQ+dHHNQM
QPu7hU1xkpLrL88ibjndn7iBjRKjT2wryp9Qk3jJKjev0bYzHeA3gEhHpBnOLK2M
Pf89y+dTX7O5wxrqmb5eTIVjBlL2GxIkl/KZ5DikHeldkpbrRnlQ4Hkef1OyBj+6
HBSqMrjBWlM/NOFD/QFiWsPU7p4TFAWxMFk31zcy9yM/3rfNXKDt+5lBFht2aosU
05QwNZ4NNy0NRIIkWy5714bL75T7evt2snlx80w6uxkCJew4E49h7Dhb4VJ87ddX
cADTYBZQkFZnY+ofakc7MTVmvsn8gtNRnEzZGGbq3QC4reLReaSv+L4eISW5Yis/
qfp3771scQAr0Xz9vSvPQVJYQ/lc7p24ZWrAR/biwgFav4dsuL02F6DOxVqxGtv9
wiw/yH755av+0O1ZcTJQOOLGiaEsBeppkYX34Wdn9o6qxTrc6ZDjU41FQVuv3iM9
ViVHafEAn1ICKmGhJRPtMCRBocuN0cCsMVRrZ/L5gLk+C1KR7sGgUncd/Gre3JIw
LiO8CucIe2St6Au8RJXFdWfN70jE17jYguMP7VQ5AvL3dn3oG9MFgp0RRMONg/I0
mY8L674VGlb3V5pLnGUBpEU1AwgKHlJYlO15AFFwBfEApZezPt56pjEDK01kx81q
A6Ghh0XXr2mFJv/IbLg+sm/ChP2jg0bqkPjJvnBdVnpQVYDtAHodAym1/GBirrIm
9Cf2xYoja3qV8tU5c7OtBLo6E+buIIQmKePa5vIIdGS3GsyssTlV35XG8FkJV+Ew
UeWPmqIbqGSjKXySzFqPuHeuwtBU4dHpl+RGbXBy0yYiw6KmZpOyVuR98bqbGomj
EaQaVHacSF5wOoyozodo19WGKlv/SkIKuIv4X2z2SqTiC9bQ2ZCCp4zEWtZX6cmf
FASmVKeS3r68LB87tPbbydCSYeTa8rpie63PHfsrJIqwK2n3lnyBBufyuK7nyr40
JiFY6gD7U2eWJ8d6YpzMvtAGhyUVsjDFffbjuNz7lx5H4equPatD5h0riqPNXUW0
DfRajWsc720eE0f7ouzsGx1TokzQWpwK3dQIYyG7lJ/OXexkYKR5bdiwnoTJf4EW
RIj3pcuk0L6s1EhfezxT7wEQbk7QhVQ4asvgNyosPjf5lYDivnwCIdvs1rjLm0Db
ZBysZl3h/IQAPtCsZvWbnr6+BIVFI2N9y8w6V5fs+6ynvwYtBlqSQ95w+V3KyE6m
BQKJ0aA5KFl7T+5KlKGg3JIqKwCsht/HCiDeBaOuDd6oHrH+Zi1tRnkwvyBLe34C
2zBrmrXrgP0oBtmcqSxE5WS0AwABnA2p0KkZSXC719haByuLIo2rQ9hC8LfsNQto
5JW6ZcPXdZaxcuGvhmphaVzaeK26/IQp7vf2XvboiApa2W0llsn9VpEMFz/VRJbo
B0v0dG72/UOmuOQ+CQJYqY5Bl0fqBfX31Z8Bg78vrBuzMNSO5ILU5ujnTle3eBcF
Jy04/sSZfTeN0Do2hYNddRX2NGA447YsebIOWCiZfPW2DDtpz7moEh28MuHANhez
7Dcl5kAwqvnDWimKloLAJbq2uMDNmyWZhLgC2iNFC/9QAAAAAQAFWC41MDkAAAUV
MIIFETCCAvmgAwIBAgIGAVn+Ni/sMA0GCSqGSIb3DQEBDQUAMG0xCzAJBgNVBAYT
AkZSMREwDwYDVQQHEwhHcmVub2JsZTENMAsGA1UEChMEQ05SUzERMA8GA1UECxMI
TFBTQy1BTUkxKTAnBgNVBAMTIEFNSSBSb290IENlcnRpZmljYXRpb24gQXV0aG9y
aXR5MB4XDTE3MDIwMjA5NDQ0MVoXDTI3MDIwMjA5NDQ0MVowVzELMAkGA1UEBhMC
RlIxETAPBgNVBAcTCEdyZW5vYmxlMQ0wCwYDVQQKEwRDTlJTMREwDwYDVQQLEwhM
UFNDLUFNSTETMBEGA1UEAwwKKi5pbjJwMy5mcjCCASIwDQYJKoZIhvcNAQEBBQAD
ggEPADCCAQoCggEBAJqlUVIFZUpSCINPM6FXuSQvBKE94UEa0jkYgfrHnaITv6xu
cHt4pRT602FRTD8/tKE8wXXcZ1TCzT/uU69AFqFlDcVNlF5aUvEZgRrhwuNZp+Gv
donLtlt8xmng7w0nR4sE33ULdrgSzH59gpiut3V6jTd0iT0p6Hr8cBGNA0+AT+D2
3HgTtBw2FNT3LjqrrZNw4oR2zRp6jbTdJK3R7z+VN7j+zu6Urpw3kFOJNB0Z8hr5
uzHu+zWr/h4GgTQG7w/ls3Ote4brTYdR+QSOq47XM0aD+CnAxgXHIGJrE4ijU4zY
e89wDSI5glhrzA/CFF5CudCIeX1NA9MEzZxhgRsCAwEAAaOBzDCByTAJBgNVHRME
AjAAMB0GA1UdDgQWBBT9e+oHt6f/eBrAlMvApQQrYFatCjCBnAYDVR0jBIGUMIGR
gBR2NSJBNHLUTOiSeaIl2DVPdTWcN6FxpG8wbTELMAkGA1UEBhMCRlIxETAPBgNV
BAcTCEdyZW5vYmxlMQ0wCwYDVQQKEwRDTlJTMREwDwYDVQQLEwhMUFNDLUFNSTEp
MCcGA1UEAxMgQU1JIFJvb3QgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHmCBgFObX+o
LTANBgkqhkiG9w0BAQ0FAAOCAgEAOQkPwXxz/UE4ZMsgk227QKbNqqcWT3jVpCf9
VhZjk9rhb16LkqlfsalW0OaXAMlEssvmWT5ueMsz3m+g0MNx+25cWUEUFhVaXYnC
5YsXC1lmCRdqkwq9eBlAO1BOkYY3zIjZWXdfqaMwJmMgWcZI3lf0PDWw4JD2J0zF
2j+06A301340TuD5/2x1SucENkkNUSuzBoFHIsLdvW0JzWQ85D5uustzDQZW1C45
ckKoH6DuWX5N9+Z4zNULXxeVmloM5aHDYQ/7a4fkJUtLFacD18T3o04gITJ7fbP9
8uHJ4K8QEVb8YynkTHh/f9Od77R3yngHq104Y750yPeMLWdz+kyu66d5ntkE6Lh6
Kad83eCH1K964GVMQzDem10MYkCNnqZGa+US03od8C5mR35ncVc4RhO38v5V8Fcz
ENjQaxMRosX8Z3uspaY0eMQrD99bhniHccEBlettHmDZXPqufEcDUn1AAXdC+RQ2
j1sKwsysBZoCAG2ZvXjHm/1py38HiwTr8KVPL70ki4HQ9z/sKEnHpDcV0VJ41abM
Oh/tPY+EWM/KE+pNRmjYqk6EGsPiH6wVCZ8KsZIg79j3ggvyaj6iK4LMRb3rwFQN
qtLgv718wc1jVbg3Nf7tpWRH4jt95dF0btQTmwqv865vG0/JP5R8O1ZfQaNjGKY3
uI+9iSpn4domatnCdu5OGeYZXEM8oxkYEQ==
EOF

  openssl base64 -d -in "${AMI_HOME}/certs/temp/ami.b64" -out "${AMI_HOME}/certs/ami.jks"

  ################

  echo "# IGTF version ${IGTF_VERSION}" > "${AMI_HOME}/certs/table.txt"

  ################

  rm -fr "${AMI_HOME}/certs/cas/"
  mkdir -p "${AMI_HOME}/certs/cas/"

  echo "IGTF version ${IGTF_VERSION}:"

  for FILE in "${AMI_HOME}/certs/temp"/*.pem
  do
    echo -e " -> ${GREEN}${FILE##*/}${NC}"

    HASH=$(openssl x509 -hash -in "${FILE}" -noout)

    if [ $? -eq 0 ]
    then
      cp "${FILE}" "${AMI_HOME}/certs/cas/c${HASH}.0"

      echo -e "${HASH}: ${FILE##*/}" >> "${AMI_HOME}/certs/table.txt"

      echo 'changeit' | "$JAVA_HOME/bin/keytool" -noprompt -keystore "${AMI_HOME}/certs/ami.jks" -importcert -alias "${HASH}" -file "${FILE}" &> /dev/null

      if [ $? -ne 0 ]
      then
        echo -e "${RED}    ** error **${NC}"
      fi
    else
      echo -e "${RED}    ** error **${NC}"
    fi
  done

  ################

  rm -fr "${AMI_HOME}/certs/temp/"

  ################

  _ok

) || _err

########################################################################################################################
########################################################################################################################

if [ "${AMI_RESTART}" != '0' ]
then
  _box "Starting AMI"

  (

    "${AMI_HOME}/bin/ami.sh" start

    _ok

  ) || _err
fi

########################################################################################################################
########################################################################################################################

_line

########################################################################################################################
########################################################################################################################
