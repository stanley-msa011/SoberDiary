package ubicomp.soberdiary.storytelling.ui;

public interface PageAnimationCaller {
	public void resetPage(int change);

	public void endFlingAnimation();

	public void endOnViewCreateAnimation();

	public int getPageWidth();

	public int getPageHeight();

	public void invalidatePage();
}
