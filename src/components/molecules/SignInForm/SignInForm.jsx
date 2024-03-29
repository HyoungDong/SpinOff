import React, { useEffect } from 'react';
import {
  Container,
  LogIn,
  InputContainer,
  PlaceHolder,
  Naver,
  Kakao,
  NaverContainer,
  KakaoContainer,
  inputStyle,
  textButtonStyle,
  textButtonStyle2,
  textButtonStyle3,
  normalButtonStyle,
  TextContainer,
} from './styles';
import { Input, NormalButton, TextButton } from '../../atoms';
import { useInput } from '../../../Hooks';
import propTypes from 'prop-types';

function SignInForm({ isOpen, modeDispatch }) {
  const idInput = useInput('');
  const pwInput = useInput('');
  useEffect(() => {
    if (!isOpen) {
      idInput.onReset();
      pwInput.onReset();
    }
  }, [isOpen]);
  return (
    <Container>
      <LogIn />
      <InputContainer>
        <Input Style={inputStyle} {...idInput} />
        <PlaceHolder value={idInput.value}>아이디</PlaceHolder>
      </InputContainer>
      <InputContainer>
        <Input Style={inputStyle} {...pwInput} type="password" />
        <PlaceHolder value={pwInput.value}> 비밀번호</PlaceHolder>
      </InputContainer>
      <NormalButton Style={normalButtonStyle}>로그인</NormalButton>
      <TextContainer>
        <TextButton Style={textButtonStyle} onClick={modeDispatch.onSignUp}>
          Spin-off 회원가입
        </TextButton>
      </TextContainer>
      <TextContainer>
        <TextButton Style={textButtonStyle2} onClick={modeDispatch.onFindInfo}>
          아이디/비밀번호 찾기
        </TextButton>
      </TextContainer>
      <NaverContainer>
        <Naver />
        <TextButton Style={textButtonStyle3}>
          네이버로
          <br />
          시작하기
        </TextButton>
      </NaverContainer>
      <KakaoContainer>
        <Kakao />
        <TextButton Style={textButtonStyle3}>
          카카오톡으로
          <br />
          시작하기
        </TextButton>
      </KakaoContainer>
    </Container>
  );
}

SignInForm.propTypes = {
  isOpen: propTypes.bool,
  modeDispatch: propTypes.object,
};
export default SignInForm;
