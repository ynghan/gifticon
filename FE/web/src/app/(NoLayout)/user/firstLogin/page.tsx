import { PayPwdForm } from '@/features/payPwdForm';
import { FadeUpContainer } from '@/widgets/fadeUpContainer';

export default function page() {
  return (
    <FadeUpContainer className="h-full">
      <section className="flex flex-col items-center gap-4 pt-16">
        <h1 className="font-bold text-2xl">반가워요 또가게입니다 😍</h1>
        <p>또페이에 등록하기 위해서는 페이 비밀번호를 설정해주세요</p>
        <PayPwdForm />
      </section>
    </FadeUpContainer>
  );
}
